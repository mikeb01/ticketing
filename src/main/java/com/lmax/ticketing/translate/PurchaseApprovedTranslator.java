package com.lmax.ticketing.translate;

import com.lmax.disruptor.EventTranslator;
import com.lmax.ticketing.api.AllocationApproved;
import com.lmax.ticketing.api.EventType;
import com.lmax.ticketing.api.Message;
import com.lmax.ticketing.api.TicketPurchase;

public class PurchaseApprovedTranslator implements EventTranslator<Message>
{
    private TicketPurchase ticketPurchase;

    @Override
    public Message translateTo(Message message, long sequence)
    {
        message.type.set(EventType.ALLOCATION_APPROVED);
        AllocationApproved allocationApproved = message.event.asAllocationApproved;
        
        allocationApproved.accountId.set(ticketPurchase.accountId.get());
        allocationApproved.requestId.set(ticketPurchase.requestId.get());
        allocationApproved.numSeats.set(ticketPurchase.numSeats.get());
        
        return message;
    }

    public void set(TicketPurchase ticketPurchase)
    {
        this.ticketPurchase = ticketPurchase;
    }
}
