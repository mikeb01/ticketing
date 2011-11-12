package com.lmax.ticketing.api;

import javolution.io.Struct;

public class ConcertCreated extends Struct
{
    public final Signed64         concertId   = new Signed64();
    public final Signed64         version     = new Signed64();
    public final UTF8String       name        = new UTF8String(32);
    public final UTF8String       venue       = new UTF8String(32);
    public final Signed16         numSections = new Signed16();
    public final SectionSeating[] sections    = array(new SectionSeating[10]);
}
