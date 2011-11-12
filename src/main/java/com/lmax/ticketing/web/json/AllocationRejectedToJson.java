package com.lmax.ticketing.web.json;

import net.minidev.json.JSONObject;

import com.lmax.ticketing.api.AllocationRejected;
import com.lmax.ticketing.api.EventType;

public class AllocationRejectedToJson
{
    public JSONObject toJson(AllocationRejected allocationRejected)
    {
        JSONObject json = new JSONObject();
        
        json.put("accountId", allocationRejected.accountId.get());
        json.put("requestId", allocationRejected.requestId.get());
        json.put("reason",    allocationRejected.reason.get());
        json.put("type",      EventType.ALLOCATION_REJECTED.name());
        
        return json;
    }
}
