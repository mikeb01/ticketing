package com.lmax.ticketing.domain;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.HashMap;

import com.google.common.collect.Maps;
import com.lmax.ticketing.api.ConcertCreated;
import com.lmax.ticketing.api.RejectionReason;
import com.lmax.ticketing.api.SectionSeating;
import com.lmax.ticketing.api.TicketPurchase;

public class ConcertService implements Concert.Observer
{
    private final ConcertServiceListener listener;
    private final Long2ObjectMap<Concert> concertRepository = new Long2ObjectOpenHashMap<Concert>();

    public ConcertService(ConcertServiceListener listener)
    {
        this.listener = listener;
    }

    public void on(TicketPurchase ticketPurchase)
    {
        Concert concert = concertRepository.get(ticketPurchase.concertId.get());
        if (concert == null)
        {
            listener.onPurchaseRejected(RejectionReason.CONCERT_DOES_NOT_EXIST, ticketPurchase);
            return;
        }
        
        Section section = concert.getSection(ticketPurchase.sectionId.get());
        if (section == null)
        {
            listener.onPurchaseRejected(RejectionReason.SECTION_DOES_NOT_EXIST, ticketPurchase);
            return;
        }
        
        int numSeats = ticketPurchase.numSeats.get();
        if (concert.getSeating(section).getAvailableSeats() < numSeats)
        {
            listener.onPurchaseRejected(RejectionReason.NOT_ENOUGH_SEATS, ticketPurchase);
            return;
        }
        
        concert.allocateSeating(section, numSeats);
        listener.onPurchaseApproved(ticketPurchase);
    }
    
    public void on(ConcertCreated eventCreated)
    {
        HashMap<Section, Seating> seatingBySection = Maps.newHashMap();
        
        for (int i = 0, n = eventCreated.numSections.get(); i < n; i++)
        {
            SectionSeating sectionSeating = eventCreated.sections[i];
            Section section = new Section(sectionSeating.sectionId.get(), 
                                          sectionSeating.name.get(),
                                          sectionSeating.price.get());
            Seating seating = new Seating(sectionSeating.seats.get());
            
            seatingBySection.put(section, seating);
        }
        
        Concert concert = new Concert(eventCreated.concertId.get(),
                                      eventCreated.name.get(),
                                      eventCreated.venue.get(),
                                      seatingBySection);
        concertRepository.put(concert.getId(), concert);
        concert.addObserver(this);
        listener.onConcertAvailable(concert);
    }
    
    @Override
    public void onSeatsAllocated(Concert event, Section section, Seating seating)
    {
        listener.onSectionUpdated(event.getId(), section.getId(), seating.getAvailableSeats());
    }
}
