package com.lmax.ticketing.api;

import javolution.io.Struct;

public class SectionUpdated extends Struct
{
    public final Signed64 concertId      = new Signed64();
    public final Signed64 sectionId      = new Signed64();
    public final Signed64 version        = new Signed64();
    public final Signed32 seatsAvailable = new Signed32();
}
