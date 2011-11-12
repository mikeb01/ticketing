package com.lmax.ticketing.api;

import javolution.io.Struct;


public class TicketPurchase extends Struct
{
    public final Signed64 concertId = new Signed64();
    public final Signed64 sectionId = new Signed64();
    public final Signed32 numSeats  = new Signed32();
    public final Signed64 accountId = new Signed64();
    public final Signed64 requestId = new Signed64();
    
    public static EventType type()
    {
        return EventType.TICKET_PURCHASE;
    }
}
