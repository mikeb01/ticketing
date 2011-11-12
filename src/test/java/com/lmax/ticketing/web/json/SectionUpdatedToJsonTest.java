package com.lmax.ticketing.web.json;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import net.minidev.json.JSONObject;

import org.junit.Test;

import com.lmax.ticketing.api.EventType;
import com.lmax.ticketing.api.SectionUpdated;

public class SectionUpdatedToJsonTest
{

    @Test
    public void shouldTranslate()
    {
        SectionUpdated sectionUpdate = new SectionUpdated();
        
        sectionUpdate.concertId.set(1231L);
        sectionUpdate.sectionId.set(6543L);
        sectionUpdate.version.set(341231L);
        sectionUpdate.seatsAvailable.set(7897);
        
        SectionUpdatedToJson translator = new SectionUpdatedToJson();
        
        JSONObject json = translator.toJson(sectionUpdate);
        
        assertThat(json.get("concertId"),      is((Object) sectionUpdate.concertId.get()));
        assertThat(json.get("sectionId"),      is((Object) sectionUpdate.sectionId.get()));
        assertThat(json.get("version"),        is((Object) sectionUpdate.version.get()));
        assertThat(json.get("seatsAvailable"), is((Object) sectionUpdate.seatsAvailable.get()));
        assertThat(json.get("type"), is((Object) EventType.SECTION_UPDATED.name()));
    }

}
