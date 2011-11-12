package com.lmax.ticketing.web.json;

import net.minidev.json.JSONObject;

import com.lmax.ticketing.api.AllocationApproved;
import com.lmax.ticketing.api.EventType;

public class AllocationApprovedToJson
{
    public JSONObject toJson(AllocationApproved allocationApproved)
    {
        JSONObject json = new JSONObject();
        
        json.put("accountId", allocationApproved.accountId.get());
        json.put("requestId", allocationApproved.requestId.get());
        json.put("numSeats", allocationApproved.numSeats.get());
        json.put("type", EventType.ALLOCATION_APPROVED.name());
        
        return json;
    }
}
