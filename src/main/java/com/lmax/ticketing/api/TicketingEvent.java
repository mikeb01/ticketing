package com.lmax.ticketing.api;

import javolution.io.Union;

public class TicketingEvent extends Union
{
    public final Poll               asPoll               = inner(new Poll());
    public final ConcertCreated     asConcertCreated     = inner(new ConcertCreated());
    public final TicketPurchase     asTicketPurchase     = inner(new TicketPurchase());
    public final AllocationApproved asAllocationApproved = inner(new AllocationApproved());
    public final AllocationRejected asAllocationRejected = inner(new AllocationRejected());
    public final SectionUpdated     asSectionUpdated     = inner(new SectionUpdated());
}
