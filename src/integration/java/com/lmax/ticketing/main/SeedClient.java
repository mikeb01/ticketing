package com.lmax.ticketing.main;

import com.lmax.ticketing.api.ConcertCreated;
import com.lmax.ticketing.api.EventType;
import com.lmax.ticketing.api.Message;
import com.lmax.ticketing.io.UdpEventHandler;

public class SeedClient
{
    public static void main(String[] args) throws Exception
    {
        UdpEventHandler udpEventHandler = new UdpEventHandler("localhost", ConcertServiceMain.SERVER_PORT);
        
        {
            Message m1 = new Message();
            
            long concertId = 1L;
            m1.type.set(EventType.CONCERT_CREATED);
            ConcertCreated concertCreated = m1.event.asConcertCreated;
            concertCreated.concertId.set(concertId);
            concertCreated.name.set("Red Hot Chili Peppers");
            concertCreated.venue.set("Albert Hall");
            concertCreated.numSections.set((short) 8);
            
            concertCreated.sections[0].sectionId.set(1);
            concertCreated.sections[0].name.set("Section A");
            concertCreated.sections[0].seats.set(Integer.MAX_VALUE);
            concertCreated.sections[0].price.set(58.50F);
            
            concertCreated.sections[1].sectionId.set(2);
            concertCreated.sections[1].name.set("Section B");
            concertCreated.sections[1].seats.set(Integer.MAX_VALUE);
            concertCreated.sections[1].price.set(63.50F);
            
            concertCreated.sections[2].sectionId.set(3);
            concertCreated.sections[2].name.set("Section C");
            concertCreated.sections[2].seats.set(Integer.MAX_VALUE);
            concertCreated.sections[2].price.set(45.50F);
            
            concertCreated.sections[3].sectionId.set(4);
            concertCreated.sections[3].name.set("Section D");
            concertCreated.sections[3].seats.set(Integer.MAX_VALUE);
            concertCreated.sections[3].price.set(67.50F);
            
            concertCreated.sections[4].sectionId.set(5);
            concertCreated.sections[4].name.set("Section E");
            concertCreated.sections[4].seats.set(Integer.MAX_VALUE);
            concertCreated.sections[4].price.set(38.00F);
            
            concertCreated.sections[5].sectionId.set(6);
            concertCreated.sections[5].name.set("Section F");
            concertCreated.sections[5].seats.set(Integer.MAX_VALUE);
            concertCreated.sections[5].price.set(66.55F);
            
            concertCreated.sections[6].sectionId.set(7);
            concertCreated.sections[6].name.set("Section G");
            concertCreated.sections[6].seats.set(Integer.MAX_VALUE);
            concertCreated.sections[6].price.set(31.95F);
            
            concertCreated.sections[7].sectionId.set(8);
            concertCreated.sections[7].name.set("Section H");
            concertCreated.sections[7].seats.set(Integer.MAX_VALUE);
            concertCreated.sections[7].price.set(78.89F);
            
            udpEventHandler.onEvent(m1, 0, true);
        }
        
        {
            Message m1 = new Message();
            
            long concertId = 2L;
            m1.type.set(EventType.CONCERT_CREATED);
            ConcertCreated concertCreated = m1.event.asConcertCreated;
            concertCreated.concertId.set(concertId);
            concertCreated.name.set("Gomez");
            concertCreated.venue.set("Wembley Park");
            concertCreated.numSections.set((short) 8);
            
            concertCreated.sections[0].sectionId.set(1);
            concertCreated.sections[0].name.set("Section A");
            concertCreated.sections[0].seats.set(Integer.MAX_VALUE);
            concertCreated.sections[0].price.set(58.50F);
            
            concertCreated.sections[1].sectionId.set(2);
            concertCreated.sections[1].name.set("Section B");
            concertCreated.sections[1].seats.set(Integer.MAX_VALUE);
            concertCreated.sections[1].price.set(63.50F);
            
            concertCreated.sections[2].sectionId.set(3);
            concertCreated.sections[2].name.set("Section C");
            concertCreated.sections[2].seats.set(Integer.MAX_VALUE);
            concertCreated.sections[2].price.set(45.50F);
            
            concertCreated.sections[3].sectionId.set(4);
            concertCreated.sections[3].name.set("Section D");
            concertCreated.sections[3].seats.set(Integer.MAX_VALUE);
            concertCreated.sections[3].price.set(67.50F);
            
            concertCreated.sections[4].sectionId.set(5);
            concertCreated.sections[4].name.set("Section E");
            concertCreated.sections[4].seats.set(Integer.MAX_VALUE);
            concertCreated.sections[4].price.set(38.00F);
            
            concertCreated.sections[5].sectionId.set(6);
            concertCreated.sections[5].name.set("Section F");
            concertCreated.sections[5].seats.set(Integer.MAX_VALUE);
            concertCreated.sections[5].price.set(66.55F);
            
            concertCreated.sections[6].sectionId.set(7);
            concertCreated.sections[6].name.set("Section G");
            concertCreated.sections[6].seats.set(Integer.MAX_VALUE);
            concertCreated.sections[6].price.set(31.95F);
            
            concertCreated.sections[7].sectionId.set(8);
            concertCreated.sections[7].name.set("Section H");
            concertCreated.sections[7].seats.set(Integer.MAX_VALUE);
            concertCreated.sections[7].price.set(78.89F);
            
            udpEventHandler.onEvent(m1, 0, true);
        }        
    }
}
