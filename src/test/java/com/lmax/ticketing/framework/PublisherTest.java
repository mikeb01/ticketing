package com.lmax.ticketing.framework;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.lmax.disruptor.EventPublisher;
import com.lmax.ticketing.api.Message;
import com.lmax.ticketing.api.RejectionReason;
import com.lmax.ticketing.api.TicketPurchase;
import com.lmax.ticketing.domain.Concert;
import com.lmax.ticketing.translate.ConcertAvailableTranslator;
import com.lmax.ticketing.translate.PurchaseApprovedTranslator;
import com.lmax.ticketing.translate.PurchaseRejectedTranslator;
import com.lmax.ticketing.translate.SectionUpdatedTranslator;

public class PublisherTest
{
    private EventPublisher<Message> eventPublisher;
    private Publisher publisher;
    
    @SuppressWarnings("unchecked")
    @Before
    public void setup()
    {
        eventPublisher = mock(EventPublisher.class);
        publisher = new Publisher(eventPublisher);
    }
    
    @Test
    public void shouldPublishPurchasedApproved()
    {
        publisher.onPurchaseApproved(new TicketPurchase());
        verify(eventPublisher).publishEvent(argThat(notNullValue(PurchaseApprovedTranslator.class)));
    }
    
    @Test
    public void shouldPublishConcertAvailable()
    {
        publisher.onConcertAvailable(mock(Concert.class));
        verify(eventPublisher).publishEvent(argThat(notNullValue(ConcertAvailableTranslator.class)));
    }
    
    @Test
    public void shouldPublishPurchaseRejected()
    {
        publisher.onPurchaseRejected(RejectionReason.CONCERT_DOES_NOT_EXIST, new TicketPurchase());
        verify(eventPublisher).publishEvent(argThat(notNullValue(PurchaseRejectedTranslator.class)));
    }
    
    @Test
    public void shouldSectionUpdated()
    {
        publisher.onSectionUpdated(1, 2, 3);
        verify(eventPublisher).publishEvent(argThat(notNullValue(SectionUpdatedTranslator.class)));
    }
}
