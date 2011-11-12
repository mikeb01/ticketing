package com.lmax.ticketing.translate;

import java.util.Map.Entry;

import com.lmax.disruptor.EventTranslator;
import com.lmax.ticketing.api.ConcertCreated;
import com.lmax.ticketing.api.EventType;
import com.lmax.ticketing.api.Message;
import com.lmax.ticketing.domain.Concert;
import com.lmax.ticketing.domain.Seating;
import com.lmax.ticketing.domain.Section;

public class ConcertAvailableTranslator implements EventTranslator<Message>
{
    private Concert concert;

    @Override
    public Message translateTo(Message message, long sequence)
    {
        message.type.set(EventType.CONCERT_CREATED);
        
        ConcertCreated concertCreated = message.event.asConcertCreated;
        
        Concert concert = this.concert;
        concertCreated.concertId.set(concert.getId());
        concertCreated.version.set(sequence);
        concertCreated.name.set(concert.getName());
        concertCreated.venue.set(concert.getVenue());
        
        short i = 0;
        for (Entry<Section, Seating> entry : concert.getSeatingMap().entrySet())
        {
            Section section = entry.getKey();
            Seating seating = entry.getValue();
            concertCreated.sections[i].sectionId.set(section.getId());
            concertCreated.sections[i].name.set(section.getName());
            concertCreated.sections[i].price.set(section.getPrice());
            concertCreated.sections[i].seats.set(seating.getAvailableSeats());
            i++;
        }
        concertCreated.numSections.set(i);
        
        return message;
    }

    public void set(Concert concert)
    {
        this.concert = concert;
    }
}
