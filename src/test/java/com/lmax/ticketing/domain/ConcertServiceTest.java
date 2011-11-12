package com.lmax.ticketing.domain;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;

import com.lmax.ticketing.api.ConcertCreated;
import com.lmax.ticketing.api.RejectionReason;
import com.lmax.ticketing.api.SectionSeating;
import com.lmax.ticketing.api.TicketPurchase;

public class ConcertServiceTest
{
    private ConcertServiceListener listener;
    private ConcertService concertService;

    @Before
    public void setup()
    {
        listener = mock(ConcertServiceListener.class);
        concertService = new ConcertService(listener);
    }

    @Test
    public void shouldSendNotifyOnNewConcertCreated()
    {
        ConcertCreated concertCreated = singeSectionConcert();
        concertService.on(concertCreated);
        
        verify(listener).onConcertAvailable(argThat(isConcert(concertCreated)));
    }
    
    @Test
    public void shouldNotifyOfSeatsAllocated() throws Exception
    {
        ConcertCreated concertCreated = singeSectionConcert();
        concertService.on(concertCreated);
        long concertId = concertCreated.concertId.get();
        long sectionId = concertCreated.sections[0].sectionId.get();
        
        TicketPurchase ticketPurchase = new TicketPurchase();
        ticketPurchase.concertId.set(concertId);
        ticketPurchase.sectionId.set(sectionId);
        ticketPurchase.numSeats.set(4);
        ticketPurchase.accountId.set(7L);
        ticketPurchase.requestId.set(11L);
        int seatsAvailable = concertCreated.sections[0].seats.get() - ticketPurchase.numSeats.get();
        
        concertService.on(ticketPurchase);
        
        verify(listener).onPurchaseApproved(ticketPurchase);
        verify(listener).onSectionUpdated(concertId, sectionId, seatsAvailable);
    }
    
    @Test
    public void shouldNotifyFailureOnNonExistentConcert() throws Exception
    {
        ConcertCreated concertCreated = singeSectionConcert();
        concertService.on(concertCreated);
        
        TicketPurchase ticketPurchase = new TicketPurchase();
        ticketPurchase.concertId.set(999999999999L);
        ticketPurchase.sectionId.set(concertCreated.sections[0].sectionId.get());
        ticketPurchase.numSeats.set(4);
        ticketPurchase.accountId.set(7L);
        ticketPurchase.requestId.set(11L);
        
        concertService.on(ticketPurchase);
        
        verify(listener).onPurchaseRejected(RejectionReason.CONCERT_DOES_NOT_EXIST, ticketPurchase);
    }
    
    @Test
    public void shouldNotifyFailureOnNonExistentSection() throws Exception
    {
        ConcertCreated concertCreated = singeSectionConcert();
        concertService.on(concertCreated);
        
        TicketPurchase ticketPurchase = new TicketPurchase();
        ticketPurchase.concertId.set(concertCreated.concertId.get());
        ticketPurchase.sectionId.set(99999999999L);
        ticketPurchase.numSeats.set(4);
        ticketPurchase.accountId.set(7L);
        ticketPurchase.requestId.set(11L);
        
        concertService.on(ticketPurchase);
        
        verify(listener).onPurchaseRejected(RejectionReason.SECTION_DOES_NOT_EXIST, ticketPurchase);
    }
    
    @Test
    public void shouldRejectOrderIfNotSeatsAvailable() throws Exception
    {
        ConcertCreated concertCreated = singeSectionConcert();
        concertService.on(concertCreated);
        
        TicketPurchase ticketPurchase = new TicketPurchase();
        ticketPurchase.concertId.set(concertCreated.concertId.get());
        ticketPurchase.sectionId.set(concertCreated.sections[0].sectionId.get());
        ticketPurchase.numSeats.set(concertCreated.sections[0].seats.get() + 10);
        ticketPurchase.accountId.set(7L);
        ticketPurchase.requestId.set(11L);
        
        concertService.on(ticketPurchase);
        
        verify(listener).onPurchaseRejected(RejectionReason.NOT_ENOUGH_SEATS, ticketPurchase);
    }
    
    private ConcertCreated singeSectionConcert()
    {
        ConcertCreated concertCreated = new ConcertCreated();
        concertCreated.concertId.set(12345L);
        concertCreated.name.set("Red Hot Chili Peppers");
        concertCreated.venue.set("Albert Hall");
        concertCreated.numSections.set((short) 1);
        concertCreated.sections[0].sectionId.set(5);
        concertCreated.sections[0].name.set("East");
        concertCreated.sections[0].price.set(75.50F);
        concertCreated.sections[0].seats.set(100);
        return concertCreated;
    }

    public static Matcher<Concert> isConcert(final ConcertCreated concertCreated)
    {
        return new TypeSafeMatcher<Concert>()
        {
            @Override
            public void describeTo(Description description)
            {
                description.appendText("id:").appendValue(concertCreated.concertId.get());
                description.appendText(", name:").appendValue(concertCreated.name.get());
                description.appendText(", venue:").appendValue(concertCreated.venue.get());
            }
            
            @Override
            public boolean matchesSafely(Concert concert)
            {
                if (concert == null)
                {
                    return false;
                }
                
                boolean result = true;
                result &= concertCreated.concertId.get() == concert.getId();
                result &= concertCreated.name.get().equals(concert.getName()); 
                result &= concertCreated.venue.get().equals(concert.getVenue());
                
                for (int i = 0, n = concertCreated.numSections.get(); i < n; i++)
                {
                    SectionSeating sectionSeating = concertCreated.sections[i];
                    Section section = concert.getSection(sectionSeating.sectionId.get());
                    Seating seating = concert.getSeating(section);
                    
                    result &= sectionSeating.name.get().equals(section.getName());
                    result &= sectionSeating.seats.get() == seating.getAvailableSeats();
                }
                
                return result;
            }
        };
    }
}
