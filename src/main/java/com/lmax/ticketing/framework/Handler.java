package com.lmax.ticketing.framework;

import com.lmax.ticketing.api.Message;

public interface Handler
{
    void onMessage(Message message);
}
