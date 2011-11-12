package com.lmax.ticketing.translate;

import com.lmax.disruptor.EventTranslator;
import com.lmax.ticketing.api.EventType;
import com.lmax.ticketing.api.Message;
import com.lmax.ticketing.api.SectionUpdated;

public class SectionUpdatedTranslator implements EventTranslator<Message>
{
    private long concertId;
    private long sectionId;
    private int seatsAvailable;

    @Override
    public Message translateTo(Message message, long sequence)
    {
        message.type.set(EventType.SECTION_UPDATED);
        
        SectionUpdated sectionUpdated = message.event.asSectionUpdated;
        
        sectionUpdated.concertId.set(concertId);
        sectionUpdated.sectionId.set(sectionId);
        sectionUpdated.version.set(sequence);
        sectionUpdated.seatsAvailable.set(seatsAvailable);
        
        return message; 
    }

    public void set(long concertId, long sectionId, int seatsAvailable)
    {
        this.concertId = concertId;
        this.sectionId = sectionId;
        this.seatsAvailable = seatsAvailable;
    }
}
