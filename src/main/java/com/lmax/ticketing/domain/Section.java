package com.lmax.ticketing.domain;

public class Section
{
    private final long id;
    private final String name;
    private final float price;

    public Section(long id, String name, float price)
    {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }
    
    public float getPrice()
    {
        return price;
    }
}
