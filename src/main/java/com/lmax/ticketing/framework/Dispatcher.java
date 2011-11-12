package com.lmax.ticketing.framework;

import com.lmax.disruptor.EventHandler;
import com.lmax.ticketing.api.EventType;
import com.lmax.ticketing.api.Message;
import com.lmax.ticketing.domain.ConcertService;

public class Dispatcher implements EventHandler<Message>
{
    private final ConcertService service;

    public Dispatcher(ConcertService service)
    {
        this.service = service;
    }
    
    public void onEvent(Message message, long sequence, boolean endOfBatch)
    {
        EventType type = (EventType) message.type.get();
        
        switch (type)
        {
        case CONCERT_CREATED:
            service.on(message.event.asConcertCreated);
            break;
            
        case TICKET_PURCHASE:
            service.on(message.event.asTicketPurchase);
            break;
        }
    }
}
