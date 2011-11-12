package com.lmax.ticketing.translate;

import com.lmax.disruptor.EventTranslator;
import com.lmax.ticketing.api.AllocationRejected;
import com.lmax.ticketing.api.EventType;
import com.lmax.ticketing.api.Message;
import com.lmax.ticketing.api.RejectionReason;
import com.lmax.ticketing.api.TicketPurchase;

public class PurchaseRejectedTranslator implements EventTranslator<Message>
{
    private RejectionReason rejectionReason;
    private TicketPurchase ticketPurchase;

    @Override
    public Message translateTo(Message message, long sequence)
    {
        message.type.set(EventType.ALLOCATION_REJECTED);
        
        AllocationRejected allocationRejected = message.event.asAllocationRejected;
        allocationRejected.accountId.set(ticketPurchase.accountId.get());
        allocationRejected.requestId.set(ticketPurchase.requestId.get());
        allocationRejected.reason.set(rejectionReason);
        
        return message;
    }

    public void set(RejectionReason rejectionReason, TicketPurchase ticketPurchase)
    {
        this.rejectionReason = rejectionReason;
        this.ticketPurchase = ticketPurchase;
    }
}
