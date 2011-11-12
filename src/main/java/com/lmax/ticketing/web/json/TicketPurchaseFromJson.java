package com.lmax.ticketing.web.json;

import net.minidev.json.JSONObject;

import com.lmax.disruptor.EventTranslator;
import com.lmax.ticketing.api.EventType;
import com.lmax.ticketing.api.Message;
import com.lmax.ticketing.api.TicketPurchase;

public class TicketPurchaseFromJson implements EventTranslator<Message>
{
    private final JSONObject object;

    public TicketPurchaseFromJson(JSONObject object)
    {
        this.object = object;
    }

    @Override
    public Message translateTo(Message message, long sequence)
    {
        message.type.set(EventType.TICKET_PURCHASE);
        
        Number concertId = (Number) object.get("concertId");
        Number sectionId = (Number) object.get("sectionId");
        Number numSeats = (Number) object.get("numSeats");
        Number accountId = (Number) object.get("accountId");
        Number requestId = (Number) object.get("requestId");
        
        TicketPurchase ticketPurchase = message.event.asTicketPurchase;
        
        ticketPurchase.concertId.set(concertId.longValue());
        ticketPurchase.sectionId.set(sectionId.longValue());
        ticketPurchase.numSeats.set(numSeats.intValue());
        ticketPurchase.accountId.set(accountId.longValue());
        ticketPurchase.requestId.set(requestId.longValue());
        
        return message;
    }
}
