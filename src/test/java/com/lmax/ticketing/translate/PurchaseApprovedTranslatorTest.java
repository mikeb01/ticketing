package com.lmax.ticketing.translate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

import com.lmax.ticketing.api.AllocationApproved;
import com.lmax.ticketing.api.EventType;
import com.lmax.ticketing.api.Message;
import com.lmax.ticketing.api.TicketPurchase;
import com.lmax.ticketing.translate.PurchaseApprovedTranslator;

public class PurchaseApprovedTranslatorTest
{
    @Test
    @SuppressWarnings("rawtypes")
    public void shouldTranslate()
    {
        PurchaseApprovedTranslator translator = new PurchaseApprovedTranslator();
        TicketPurchase ticketPurchase = new TicketPurchase();
        ticketPurchase.accountId.set(11L);
        ticketPurchase.requestId.set(13L);
        ticketPurchase.numSeats.set(4);
        ticketPurchase.concertId.set(17L);
        ticketPurchase.sectionId.set(21L);
        
        translator.set(ticketPurchase);
        
        Message output = translator.translateTo(new Message(), 0);
        
        assertThat(output.type.get(), is((Enum) EventType.ALLOCATION_APPROVED));
        AllocationApproved allocationApproved = output.event.asAllocationApproved;
        assertThat(allocationApproved.accountId.get(), is(ticketPurchase.accountId.get()));
        assertThat(allocationApproved.requestId.get(), is(ticketPurchase.requestId.get()));
        assertThat(allocationApproved.numSeats.get(), is(ticketPurchase.numSeats.get()));
    }
}
