package com.lmax.ticketing.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class EventTest
{

    @Test
    public void shouldAllocateSeats()
    {
        Section section = new Section(13L, "Section A", 56.7F);
        Seating seating = new Seating(1000);
        Concert event = new Concert(17L, "Concert", "Albert Hall", ImmutableMap.of(section, seating));
        
        Concert.Observer observer = mock(Concert.Observer.class);
        event.addObserver(observer);
        
        event.allocateSeating(section, 2);
        
        verify(observer).onSeatsAllocated(event, section, seating);
        verifyNoMoreInteractions(observer);
        assertThat(event.getSeating(section).getAvailableSeats(), is(998));
    }

}
