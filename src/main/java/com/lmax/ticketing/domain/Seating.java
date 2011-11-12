package com.lmax.ticketing.domain;

public class Seating
{
    private int availableSeats;

    public Seating(int numAvailableSeats)
    {
        this.availableSeats = numAvailableSeats;
    }

    public int getAvailableSeats()
    {
        return availableSeats;
    }

    public void reserve(int seats)
    {
        availableSeats -= seats;
    }

}
