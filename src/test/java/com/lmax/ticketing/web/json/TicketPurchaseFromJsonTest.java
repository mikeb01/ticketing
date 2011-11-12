package com.lmax.ticketing.web.json;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import org.junit.Test;

import com.lmax.ticketing.api.EventType;
import com.lmax.ticketing.api.Message;
import com.lmax.ticketing.api.TicketPurchase;

public class TicketPurchaseFromJsonTest
{

    @SuppressWarnings("rawtypes")
    @Test
    public void shouldParseValidJson() throws ParseException
    {
        String json = "{\"concertId\":4000000000,\"sectionId\":5,\"numSeats\":2,\"accountId\":3411,\"requestId\":9123}";
        JSONParser parser = new JSONParser(JSONParser.MODE_RFC4627);
        JSONObject object = (JSONObject) parser.parse(json);
        
        TicketPurchaseFromJson ticketPurchaseFromJson = new TicketPurchaseFromJson(object);
        Message message = ticketPurchaseFromJson.translateTo(new Message(), 0);
        
        assertThat(message.type.get(), is((Enum) EventType.TICKET_PURCHASE));
        TicketPurchase ticketPurchase = message.event.asTicketPurchase;
        assertThat(ticketPurchase.concertId.get(), is(4000000000L));
        assertThat(ticketPurchase.sectionId.get(), is(5L));
        assertThat(ticketPurchase.numSeats.get(), is(2));
        assertThat(ticketPurchase.accountId.get(), is(3411L));
        assertThat(ticketPurchase.requestId.get(), is(9123L));
    }

}
