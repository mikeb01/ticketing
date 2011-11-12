package com.lmax.ticketing.web;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.ticketing.api.AllocationApproved;
import com.lmax.ticketing.api.AllocationRejected;
import com.lmax.ticketing.api.ConcertCreated;
import com.lmax.ticketing.api.EventType;
import com.lmax.ticketing.api.Message;
import com.lmax.ticketing.api.SectionUpdated;
import com.lmax.ticketing.io.UdpDataSource;
import com.lmax.ticketing.web.json.AllocationApprovedToJson;
import com.lmax.ticketing.web.json.AllocationRejectedToJson;
import com.lmax.ticketing.web.json.ConcertCreatedToJson;
import com.lmax.ticketing.web.json.SectionUpdatedToJson;

@SuppressWarnings("serial")
@WebServlet(asyncSupported = true)
public class ResponseServlet extends HttpServlet implements EventHandler<Message>
{
    private static final String DATA_KEY = "data";
    private static final Logger LOGGER = Logger.getLogger(RequestServlet.class.getName());
    private final Executor executor = Executors.newCachedThreadPool();
    private RingBuffer<Message> ringBuffer;

    // Event data
    private final Long2ObjectMap<JSONArray>   eventsByAccountId   = new Long2ObjectOpenHashMap<JSONArray>();
    private final Long2ObjectMap<JSONObject>  concertsByConcertId = new Long2ObjectOpenHashMap<JSONObject>();
    private final Map<SectionKey, JSONObject> sectionUpdatedByKey = new HashMap<SectionKey, JSONObject>();
    
    // Translators
    private final ConcertCreatedToJson     concertCreatedToJson     = new ConcertCreatedToJson();
    private final SectionUpdatedToJson     sectionUpdatedToJson     = new SectionUpdatedToJson(); 
    private final AllocationApprovedToJson allocationApprovedToJson = new AllocationApprovedToJson();
    private final AllocationRejectedToJson allocationRejectedToJson = new AllocationRejectedToJson();
    
    // Current contexts
    private final ConcurrentMap<Long, AsyncContext> contextsByAccount = new ConcurrentHashMap<Long, AsyncContext>();
    
    @SuppressWarnings("unchecked")
    @Override
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        
        try
        {
            int port = Integer.parseInt(config.getInitParameter("port"));

            Disruptor<Message> disruptor = new Disruptor<Message>(Message.FACTORY, 1024, executor);
            disruptor.handleEventsWith(this);
            ringBuffer = disruptor.start();
            
            UdpDataSource udpDataSource = new UdpDataSource(ringBuffer, port);
            udpDataSource.bind();
            executor.execute(udpDataSource);
            
            LOGGER.info("Listening on :" + port);
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }
    }
    
    @Override
    public void onEvent(Message message, long sequence, boolean endOfBatch) throws Exception
    {
        EventType type = (EventType) message.type.get();
        
        switch (type)
        {
        case CONCERT_CREATED:
            final ConcertCreated concertCreated = message.event.asConcertCreated;
            JSONObject concertCreatedAsJson = concertCreatedToJson.toJson(concertCreated);
            concertsByConcertId.put(concertCreated.concertId.get(), concertCreatedAsJson);
            enqueueEvent(concertCreatedAsJson);
            break;
            
        case SECTION_UPDATED:
            final SectionUpdated sectionUpdated = message.event.asSectionUpdated;
            JSONObject sectionUpdatedAsJson = sectionUpdatedToJson.toJson(sectionUpdated);
            sectionUpdatedByKey.put(sectionKeyFrom(sectionUpdated), sectionUpdatedAsJson);
            enqueueEvent(sectionUpdatedAsJson);
            break;
            
        case ALLOCATION_APPROVED:
            final AllocationApproved approval = message.event.asAllocationApproved;
            enqueueEvent(approval.accountId.get(), allocationApprovedToJson.toJson(approval));
            break;
            
        case ALLOCATION_REJECTED:
            final AllocationRejected rejection = message.event.asAllocationRejected;
            enqueueEvent(rejection.accountId.get(), allocationRejectedToJson.toJson(rejection));
            break;
            
        case POLL:
            long accountId = message.event.asPoll.accountId.get();
            long version   = message.event.asPoll.version.get();
            JSONArray events = eventsByAccountId.get(accountId);
            
            events = getUpdatedValues(events, concertsByConcertId.values(), version);
            events = getUpdatedValues(events, sectionUpdatedByKey.values(), version);
            
            if (null != events && !events.isEmpty())
            {
                dispatch(accountId, events);
            }
            break;
        }
    }

    private JSONArray getUpdatedValues(JSONArray events, Collection<JSONObject> values, long version)
    {
        for (JSONObject value : values)
        {
            long valueVersion = (Long) value.get("version");
            if (version < valueVersion)
            {
                if (null == events)
                {
                    events = new JSONArray();
                }
                
                events.add(value);
            }
        }
        return events;
    }

    private void enqueueEvent(JSONObject jsonEvent)
    {
        for (Long accountId : contextsByAccount.keySet())
        {
            JSONArray events = getEventsForAccount(accountId);
            events.add(jsonEvent);
            
            dispatch(accountId, events);
        }
    }

    private void enqueueEvent(long accountId, JSONObject jsonEvent)
    {
//        if (!contextsByAccount.containsKey(accountId))
//        {
//            return;
//        }
        
        JSONArray events = getEventsForAccount(accountId);
        events.add(jsonEvent);
        
        dispatch(accountId, events);
    }

    private void dispatch(long accountId, JSONArray events)
    {
        AsyncContext context = contextsByAccount.remove(accountId);
        
        if (null != context)
        {
            ServletRequest request = context.getRequest();
            request.setAttribute(DATA_KEY, events);
            context.dispatch();
            
            eventsByAccountId.remove(accountId);
        }
    }

    private JSONArray getEventsForAccount(long accountId)
    {
        JSONArray queue = eventsByAccountId.get(accountId);
        if (queue == null)
        {
            queue = new JSONArray();
            eventsByAccountId.put(accountId, queue);
        }
        
        return queue;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        JSONArray events = (JSONArray) req.getAttribute(DATA_KEY);
        if (null == events)
        {
            long accountId = Long.parseLong(req.getParameter("account"));
            long version = Long.parseLong(req.getParameter("version"));
            contextsByAccount.put(accountId, req.startAsync(req, res));
            
            long next = ringBuffer.next();
            Message message = ringBuffer.get(next);
            message.type.set(EventType.POLL);
            message.event.asPoll.accountId.set(accountId);
            message.event.asPoll.version.set(version);
            ringBuffer.publish(next);
        }
        else
        {
            PrintWriter writer = res.getWriter();
            JSONValue.writeJSONString(events, writer);
            writer.flush();
            writer.close();
        }
    }
    
    private SectionKey sectionKeyFrom(final SectionUpdated sectionUpdated)
    {
        return new SectionKey(sectionUpdated.concertId.get(), sectionUpdated.sectionId.get());
    }
    
    private static class SectionKey
    {
        private final long concertId;
        private final long sectionId;

        public SectionKey(long concertId, long sectionId)
        {
            this.concertId = concertId;
            this.sectionId = sectionId;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (concertId ^ (concertId >>> 32));
            result = prime * result + (int) (sectionId ^ (sectionId >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            SectionKey other = (SectionKey) obj;
            if (concertId != other.concertId)
                return false;
            if (sectionId != other.sectionId)
                return false;
            return true;
        }
    }
}
