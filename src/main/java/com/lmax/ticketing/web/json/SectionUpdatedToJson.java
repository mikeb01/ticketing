package com.lmax.ticketing.web.json;

import com.lmax.ticketing.api.EventType;
import com.lmax.ticketing.api.SectionUpdated;

import net.minidev.json.JSONObject;

public class SectionUpdatedToJson
{
    public JSONObject toJson(SectionUpdated sectionUpdated)
    {
        JSONObject json = new JSONObject();
        
        json.put("concertId",      sectionUpdated.concertId.get());
        json.put("sectionId",      sectionUpdated.sectionId.get());
        json.put("version",        sectionUpdated.version.get());
        json.put("seatsAvailable", sectionUpdated.seatsAvailable.get());
        json.put("type",           EventType.SECTION_UPDATED.name());
        
        return json;
    }
}
