package com.lmax.ticketing.api;

import java.nio.ByteBuffer;

import javolution.io.Struct;

import com.lmax.disruptor.EventFactory;

public class Message extends Struct
{
    public final Enum32<EventType> type  = new Enum32<EventType>(EventType.values());
    public final TicketingEvent    event = inner(new TicketingEvent());
    
    public int getSize(Struct s)
    {
        return (this.size() - event.size()) + s.size();
    }
    
    public int getSize()
    {
        EventType type = (EventType) this.type.get();
        return getSize(type.getStruct());
    }
    
    @Override
    public String toString()
    {
        return "Message [type=" + type.get() + "]";
    }

    public final static EventFactory<Message> FACTORY = new EventFactory<Message>()
    {
        @Override
        public Message newInstance()
        {
            Message message = new Message();
            message.setByteBuffer(ByteBuffer.allocate(message.size()), 0);
            return message;
        }
    };
}
