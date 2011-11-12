package com.lmax.ticketing.api;

import javolution.io.Struct;

public class Poll extends Struct
{
    public final Signed64 accountId = new Signed64();
    public final Signed64 version   = new Signed64();
}
