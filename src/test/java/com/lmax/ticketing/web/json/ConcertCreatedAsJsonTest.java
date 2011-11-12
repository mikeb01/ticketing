package com.lmax.ticketing.web.json;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import org.junit.Test;

import com.lmax.ticketing.api.ConcertCreated;
import com.lmax.ticketing.api.EventType;

public class ConcertCreatedAsJsonTest
{

    @Test
    public void shouldConvetToJson()
    {
        ConcertCreated concertCreated = new ConcertCreated();
        concertCreated.concertId.set(12345L);
        concertCreated.version.set(345L);
        concertCreated.name.set("Red Hot Chili Peppers");
        concertCreated.venue.set("Albert Hall");
        concertCreated.numSections.set((short) 2);
        concertCreated.sections[0].sectionId.set(5);
        concertCreated.sections[0].name.set("East");
        concertCreated.sections[0].price.set(75.50F);
        concertCreated.sections[0].seats.set(100);
        concertCreated.sections[1].sectionId.set(6);
        concertCreated.sections[1].name.set("West");
        concertCreated.sections[1].price.set(76.50F);
        concertCreated.sections[1].seats.set(200);
        
        ConcertCreatedToJson tranlator = new ConcertCreatedToJson();
        JSONObject json = tranlator.toJson(concertCreated);
        
        assertThat(json.get("concertId"), is((Object) concertCreated.concertId.get()));
        assertThat(json.get("version"),   is((Object) concertCreated.version.get()));
        assertThat(json.get("name"),      is((Object) concertCreated.name.get()));
        assertThat(json.get("venue"),     is((Object) concertCreated.venue.get()));
        assertThat(json.get("type"),      is((Object) EventType.CONCERT_CREATED.name()));
        
        assertThat(json.get("sections"),  instanceOf(JSONArray.class));
        JSONArray sections = (JSONArray) json.get("sections");
        assertThat(sections.size(), is((int) concertCreated.numSections.get()));
        
        JSONObject section1 = (JSONObject) sections.get(0);        
        assertThat(section1.get("sectionId"), is((Object) concertCreated.sections[0].sectionId.get()));
        assertThat(section1.get("name"),      is((Object) concertCreated.sections[0].name.get()));
        assertThat(section1.get("price"),     is((Object) concertCreated.sections[0].price.get()));
        assertThat(section1.get("seats"),     is((Object) concertCreated.sections[0].seats.get()));
        
        JSONObject section2 = (JSONObject) sections.get(1);        
        assertThat(section2.get("sectionId"), is((Object) concertCreated.sections[1].sectionId.get()));
        assertThat(section2.get("name"),      is((Object) concertCreated.sections[1].name.get()));
        assertThat(section2.get("price"),     is((Object) concertCreated.sections[1].price.get()));
        assertThat(section2.get("seats"),     is((Object) concertCreated.sections[1].seats.get()));
    }

}
