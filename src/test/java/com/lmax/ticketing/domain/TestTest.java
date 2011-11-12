package com.lmax.ticketing.domain;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javolution.io.Union;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.junit.Test;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.ticketing.api.EventType;
import com.lmax.ticketing.api.Message;
import com.lmax.ticketing.io.UdpEventHandler;
import com.lmax.ticketing.main.ConcertServiceMain;

public class TestTest
{

    @Test
    public void test()
    {
        Foo f = new Foo();
        
        f.v1.set(45);
        f.v2.set(234.5F);
        
        System.out.println(f.v1.get());
    }
    
    @Test
    public void jsonTest()
    {
        JSONObject o1 = new JSONObject();
        o1.put("a", 1);
        JSONObject o2 = new JSONObject();
        o2.put("b", 2);
        
        JSONArray arr = new JSONArray();
        arr.add(o1);
        arr.add(o2);
        
        System.out.println(JSONValue.toJSONString(arr));
    }
    
    @Test
    public void uriStuff() throws URISyntaxException
    {
        URI uri = new URI("http://localhost:7070/response");
        
        System.out.println(uri.relativize(new URI("?account=12&version=5")));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void pushEvent() throws InterruptedException
    {
        Executor executor = Executors.newCachedThreadPool();
        Disruptor<Message> requestDisruptor = new Disruptor<Message>(Message.FACTORY, 1024, executor);
        requestDisruptor.handleEventsWith(new UdpEventHandler("localhost", ConcertServiceMain.CLIENT_PORT));
        RingBuffer<Message> requestBuffer1 = requestDisruptor.start();
        
        RingBuffer<Message> requestBuffer = requestBuffer1;
        
        for (int i = 0; i < 10; i++)
        {
            long next = requestBuffer.next();        
            Message m = requestBuffer.get(next);
            m.type.set(EventType.ALLOCATION_APPROVED);
            m.event.asAllocationApproved.accountId.set(12);
            m.event.asAllocationApproved.requestId.set(10);
            m.event.asAllocationApproved.numSeats.set(1);
            requestBuffer.publish(next);
        }
        
        Thread.sleep(1000);
    }
}

class Foo extends Union
{
    public final Signed32 v1 = new Signed32();
    public final Float32 v2 = new Float32();
}