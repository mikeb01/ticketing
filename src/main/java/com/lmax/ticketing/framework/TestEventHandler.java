package com.lmax.ticketing.framework;

import com.lmax.disruptor.EventHandler;
import com.lmax.ticketing.api.Message;

public class TestEventHandler implements EventHandler<Message>
{
    @Override
    public void onEvent(Message message, long sequence, boolean endOfBatch) throws Exception
    {
        System.out.println("Message Type: " + message.type.get());
    }
}
