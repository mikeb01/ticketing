package com.lmax.ticketing.framework;

import com.lmax.disruptor.EventPublisher;
import com.lmax.ticketing.api.Message;
import com.lmax.ticketing.api.RejectionReason;
import com.lmax.ticketing.api.TicketPurchase;
import com.lmax.ticketing.domain.Concert;
import com.lmax.ticketing.domain.ConcertServiceListener;
import com.lmax.ticketing.translate.ConcertAvailableTranslator;
import com.lmax.ticketing.translate.PurchaseApprovedTranslator;
import com.lmax.ticketing.translate.PurchaseRejectedTranslator;
import com.lmax.ticketing.translate.SectionUpdatedTranslator;

public class Publisher implements ConcertServiceListener
{
    private final EventPublisher<Message> eventPublisher;
    private final PurchaseApprovedTranslator purchaseApprovedTranslator = new PurchaseApprovedTranslator();
    private final ConcertAvailableTranslator concertAvailableTranslator = new ConcertAvailableTranslator();
    private final PurchaseRejectedTranslator purchaseRejectedTranslator = new PurchaseRejectedTranslator();
    private final SectionUpdatedTranslator   sectionUpdatedTranslator   = new SectionUpdatedTranslator();
    
    public Publisher(EventPublisher<Message> eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public void onConcertAvailable(Concert concert)
    {
        concertAvailableTranslator.set(concert);
        eventPublisher.publishEvent(concertAvailableTranslator);
    }
    
    @Override
    public void onPurchaseApproved(TicketPurchase ticketPurchase)
    {
        purchaseApprovedTranslator.set(ticketPurchase);
        eventPublisher.publishEvent(purchaseApprovedTranslator);
    }
    
    @Override
    public void onPurchaseRejected(RejectionReason rejectionReason, TicketPurchase ticketPurchase)
    {
        purchaseRejectedTranslator.set(rejectionReason, ticketPurchase);
        eventPublisher.publishEvent(purchaseRejectedTranslator);
    }
    
    @Override
    public void onSectionUpdated(long concertId, long sectionId, int seatsAvailable)
    {
        sectionUpdatedTranslator.set(concertId, sectionId, seatsAvailable);
        eventPublisher.publishEvent(sectionUpdatedTranslator);
    }
}
