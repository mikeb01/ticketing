package com.lmax.ticketing.framework;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.lmax.ticketing.api.EventType;
import com.lmax.ticketing.api.Message;
import com.lmax.ticketing.domain.ConcertService;

public class DispatcherTest
{
    private ConcertService service;
    private Dispatcher dispatcher;

    @Before
    public void setup()
    {
        service = mock(ConcertService.class);
        dispatcher = new Dispatcher(service);
    }

    @Test
    public void shouldCallOnConcertCreated()
    {
        Message m = new Message();
        m.type.set(EventType.CONCERT_CREATED);
        dispatcher.onEvent(m, 0, true);
        
        verify(service).on(m.event.asConcertCreated);
    }

    @Test
    public void shouldCallOnTicketPurchased()
    {
        Message m = new Message();
        m.type.set(EventType.TICKET_PURCHASE);
        dispatcher.onEvent(m, 0, true);
        
        verify(service).on(m.event.asTicketPurchase);
    }
}
