package com.lmax.ticketing.web;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import com.lmax.disruptor.EventPublisher;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.ticketing.api.Message;
import com.lmax.ticketing.io.UdpEventHandler;
import com.lmax.ticketing.web.json.TicketPurchaseFromJson;

@SuppressWarnings("serial")
public class RequestServlet extends HttpServlet
{
    private static final Logger LOGGER = Logger.getLogger(RequestServlet.class.getName());
    private EventPublisher<Message> eventPublisher;

    @SuppressWarnings("unchecked")
    @Override
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        
        String host = config.getInitParameter("host");
        int port = Integer.parseInt(config.getInitParameter("port"));
        
        LOGGER.info("Connect to " + host + ":" + port);
        
        Disruptor<Message> disruptor = new Disruptor<Message>(Message.FACTORY, 1024, newSingleThreadExecutor());
        UdpEventHandler handler = new UdpEventHandler(host, port);
        disruptor.handleEventsWith(handler);
        
        RingBuffer<Message> ringBuffer = disruptor.start();
        eventPublisher = new EventPublisher<Message>(ringBuffer);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        JSONParser parser = new JSONParser(JSONParser.MODE_RFC4627);
        try
        {
            JSONObject request = (JSONObject) parser.parse(req.getReader());
            eventPublisher.publishEvent(new TicketPurchaseFromJson(request));
        }
        catch (Exception e)
        {
            throw new ServletException("Invalid input", e);
        }
    }
}
