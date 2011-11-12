package com.lmax.ticketing.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.ByteBuffer;

import org.junit.Test;

public class MessageTest
{
    @Test
    public void shoultdCreateTicketPurchaseMessageOfReusableBuffer()
    {
        ByteBuffer buffer = ByteBuffer.allocate(408);
        Message message = new Message();
        message.setByteBuffer(buffer, 0);
        message.type.set(TicketPurchase.type());
        TicketPurchase ticketPurchase = message.event.asTicketPurchase;
        ticketPurchase.concertId.set(2);
        ticketPurchase.sectionId.set(3);
        ticketPurchase.numSeats.set(4);
        ticketPurchase.accountId.set(1);
        ticketPurchase.requestId.set(-1);
        
        Message message2 = new Message();
        message2.setByteBuffer(message.getByteBuffer(), 0);
        
        System.out.println(message2.getSize(ticketPurchase));
        System.out.println(message2);
    }

    @Test
    public void shoultdCreateEventCreatedMessageOfReusableBuffer()
    {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        Message message = new Message();
        message.setByteBuffer(buffer, 0);
        message.type.set(TicketPurchase.type());
        ConcertCreated concertCreated = message.event.asConcertCreated;
        concertCreated.numSections.set((short) 1);
        concertCreated.sections[0].name.set("Wing A");
        concertCreated.sections[0].price.set(56.75F);
        concertCreated.sections[0].sectionId.set(123456789L);
        
        Message message2 = new Message();
        message2.setByteBuffer(message.getByteBuffer(), 0);
        assertThat(message2.event.asConcertCreated.sections[0].price.get(), is(56.75F));
    }
}
