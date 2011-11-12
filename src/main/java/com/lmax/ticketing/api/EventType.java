package com.lmax.ticketing.api;

import javolution.io.Struct;

public enum EventType
{
    TICKET_PURCHASE(new TicketPurchase()),
    CONCERT_CREATED(new ConcertCreated()),
    ALLOCATION_APPROVED(new AllocationApproved()),
    ALLOCATION_REJECTED(new AllocationRejected()),
    SECTION_UPDATED(new SectionUpdated()),
    POLL(new Poll());
    
    private Struct struct;

    private EventType(Struct struct)
    {
        this.struct = struct;
    }

    public Struct getStruct()
    {
        return struct;
    }
}
