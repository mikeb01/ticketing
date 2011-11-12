package com.lmax.ticketing.web.json;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import net.minidev.json.JSONObject;

import org.junit.Test;

import com.lmax.ticketing.api.AllocationApproved;
import com.lmax.ticketing.api.EventType;

public class AllocationApprovedToJsonTest
{

    @Test
    public void shouldConvertToJson()
    {
        AllocationApprovedToJson approvedToJson = new AllocationApprovedToJson();
        
        AllocationApproved allocationApproved = new AllocationApproved();
        
        allocationApproved.accountId.set(12345);
        allocationApproved.numSeats.set(2);
        allocationApproved.requestId.set(67234234L);
        
        JSONObject json = approvedToJson.toJson(allocationApproved);
        
        assertThat(json.get("accountId"), is((Object) allocationApproved.accountId.get()));
        assertThat(json.get("numSeats"), is((Object) allocationApproved.numSeats.get()));
        assertThat(json.get("requestId"), is((Object) allocationApproved.requestId.get()));
        assertThat(json.get("type"), is((Object) EventType.ALLOCATION_APPROVED.name()));
    }
}
