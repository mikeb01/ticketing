package com.lmax.ticketing.domain;

import static com.google.common.base.Preconditions.checkArgument;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Concert
{
    private final long id;
    private final String name;
    private final String venue;
    private final Map<Section, Seating> availableSeating;
    private final Long2ObjectMap<Section> sectionById = new Long2ObjectOpenHashMap<Section>();
    private final List<Observer> observers = new ArrayList<Observer>();
    
    public Concert(long id, String name, String venue, Map<Section, Seating> seating)
    {
        this.id = id;
        this.name = name;
        this.venue = venue;
        this.availableSeating = seating;
        for (Section section : seating.keySet())
        {
            sectionById.put(section.getId(), section);
        }
    }
    
    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getVenue()
    {
        return venue;
    }

    public Section getSection(long sectionId)
    {
        return sectionById.get(sectionId);
    }

    public void allocateSeating(Section section, int seats)
    {
        Seating seating = availableSeating.get(section);
        checkArgument(seating != null, "Section not found");
        checkArgument(seats <= seating.getAvailableSeats(), "Not enough seats");
        
        seating.reserve(seats);
        notifyObservers(section, seating);
    }

    private void notifyObservers(Section section, Seating seating)
    {
        for (int i = 0, n = observers.size(); i < n; i++)
        {
            Observer o = observers.get(i);
            o.onSeatsAllocated(this, section, seating);
        }
    }

    public Seating getSeating(Section section)
    {
        return availableSeating.get(section);
    }

    public Map<Section, Seating> getSeatingMap()
    {
        return availableSeating;
    }

    public void addObserver(Observer observer)
    {
        observers.add(observer);
    }

    public interface Observer
    {
        void onSeatsAllocated(Concert event, Section section, Seating seating);
    }

    @Override
    public String toString()
    {
        return "Concert [id=" + id + ", name=" + name + ", venue=" + venue + ", availableSeating=" + availableSeating
               + "]";
    }
}
