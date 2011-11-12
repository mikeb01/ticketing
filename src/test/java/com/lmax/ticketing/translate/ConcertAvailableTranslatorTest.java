package com.lmax.ticketing.translate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.google.common.collect.Maps;
import com.lmax.ticketing.api.ConcertCreated;
import com.lmax.ticketing.api.EventType;
import com.lmax.ticketing.api.Message;
import com.lmax.ticketing.domain.Concert;
import com.lmax.ticketing.domain.Seating;
import com.lmax.ticketing.domain.Section;

public class ConcertAvailableTranslatorTest
{

    @Test
    @SuppressWarnings("rawtypes")
    public void shouldTranslate()
    {
        ConcertAvailableTranslator translator = new ConcertAvailableTranslator();
        
        Map<Section, Seating> seating = Maps.newLinkedHashMap();
        seating.put(new Section(1234L, "Wing A", 34.50F), new Seating(20));
        seating.put(new Section(4567L, "Wing B", 55.40F), new Seating(40));
        
        Concert concert = new Concert(1234L, "Red Hot Chili Peppers", "Albert Hall", seating);
        translator.set(concert);
        
        Message output = translator.translateTo(new Message(), 0);
        
        assertThat(output.type.get(), is((Enum) EventType.CONCERT_CREATED));
        ConcertCreated concertCreated = output.event.asConcertCreated;
        assertThat(concertCreated.concertId.get(), is(concert.getId()));
        assertThat(concertCreated.name.get(), is(concert.getName()));
        assertThat(concertCreated.venue.get(), is(concert.getVenue()));
        assertThat(concertCreated.numSections.get(), is((short) seating.size()));
        
        int i = 0;
        for (Entry<Section, Seating> entry : seating.entrySet())
        {
            assertThat(concertCreated.sections[i].sectionId.get(), is(entry.getKey().getId()));
            assertThat(concertCreated.sections[i].seats.get(), is(entry.getValue().getAvailableSeats()));
            assertThat(concertCreated.sections[i].price.get(), is(entry.getKey().getPrice()));
            i++;
        }
    }

}
