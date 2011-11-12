package com.lmax.ticketing.api;

import javolution.io.Struct;

public class SectionSeating extends Struct
{
    public final Signed64   sectionId = new Signed64();
    public final UTF8String name      = new UTF8String(16);
    public final Float32    price     = new Float32();
    public final Signed32   seats     = new Signed32();
}
