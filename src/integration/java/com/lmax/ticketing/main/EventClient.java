package com.lmax.ticketing.main;

import com.lmax.ticketing.api.ConcertCreated;
import com.lmax.ticketing.api.EventType;
import com.lmax.ticketing.api.Message;
import com.lmax.ticketing.api.TicketPurchase;
import com.lmax.ticketing.io.UdpEventHandler;

public class EventClient
{
    public static void main(String[] args) throws Exception
    {
        UdpEventHandler udpEventHandler = new UdpEventHandler("localhost", ConcertServiceMain.SERVER_PORT);
        long concertId = System.currentTimeMillis();
        
        Message m1 = new Message();
        
        m1.type.set(EventType.CONCERT_CREATED);
        ConcertCreated concertCreated = m1.event.asConcertCreated;
        concertCreated.concertId.set(concertId);
        concertCreated.name.set("Chilis");
        concertCreated.venue.set("Albert Hall");
        concertCreated.numSections.set((short) 1);
        concertCreated.sections[0].sectionId.set(456);
        concertCreated.sections[0].name.set("Wing 5");
        concertCreated.sections[0].seats.set(Integer.MAX_VALUE);
        concertCreated.sections[0].price.set(13.50F);
        udpEventHandler.onEvent(m1, 0, true);
        
        for (int i = 0; i < 1; i++)
        {
            Message m2 = new Message();
            m2.type.set(EventType.TICKET_PURCHASE);
            TicketPurchase ticketPurchase = m2.event.asTicketPurchase;
            ticketPurchase.concertId.set(concertId);
            ticketPurchase.sectionId.set(456);
            ticketPurchase.numSeats.set(1);
            ticketPurchase.accountId.set(1001);
            ticketPurchase.requestId.set(2002 + i);
            udpEventHandler.onEvent(m2, 0, true);
        }
    }
}
