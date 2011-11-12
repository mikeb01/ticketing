package com.lmax.ticketing.domain;

import com.lmax.ticketing.api.RejectionReason;
import com.lmax.ticketing.api.TicketPurchase;

public interface ConcertServiceListener
{
    void onConcertAvailable(Concert concert);
    void onPurchaseApproved(TicketPurchase ticketPurchase);
    void onPurchaseRejected(RejectionReason rejectionReason, TicketPurchase ticketPurchase);
    void onSectionUpdated(long concertId, long sectionId, int seatsAvailable);
}
