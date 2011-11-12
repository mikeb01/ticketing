package com.lmax.ticketing.translate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

import com.lmax.ticketing.api.AllocationRejected;
import com.lmax.ticketing.api.EventType;
import com.lmax.ticketing.api.Message;
import com.lmax.ticketing.api.RejectionReason;
import com.lmax.ticketing.api.TicketPurchase;

public class PurchaseRejectedTranslatorTest
{

    @SuppressWarnings("rawtypes")
    @Test
    public void shouldTranslate()
    {
        PurchaseRejectedTranslator translator = new PurchaseRejectedTranslator();
        TicketPurchase ticketPurchase = new TicketPurchase();
        ticketPurchase.accountId.set(11L);
        ticketPurchase.requestId.set(13L);
        ticketPurchase.numSeats.set(4);
        ticketPurchase.concertId.set(17L);
        ticketPurchase.sectionId.set(21L);
        
        translator.set(RejectionReason.NOT_ENOUGH_SEATS, ticketPurchase);

        Message output = translator.translateTo(new Message(), 0);
        
        assertThat(output.type.get(), is((Enum) EventType.ALLOCATION_REJECTED));
        
        AllocationRejected allocationRejected = output.event.asAllocationRejected;
        assertThat(allocationRejected.accountId.get(), is(ticketPurchase.accountId.get()));
        assertThat(allocationRejected.requestId.get(), is(ticketPurchase.requestId.get()));
        assertThat(allocationRejected.reason.get(), is((Enum) RejectionReason.NOT_ENOUGH_SEATS));
    }
}
