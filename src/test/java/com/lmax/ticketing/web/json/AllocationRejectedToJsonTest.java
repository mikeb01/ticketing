package com.lmax.ticketing.web.json;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import net.minidev.json.JSONObject;

import org.junit.Test;

import com.lmax.ticketing.api.AllocationRejected;
import com.lmax.ticketing.api.EventType;
import com.lmax.ticketing.api.RejectionReason;

public class AllocationRejectedToJsonTest
{

    @Test
    public void shouldTranslateToJson()
    {
        AllocationRejected allocationRejected = new AllocationRejected();
        allocationRejected.accountId.set(12345L);
        allocationRejected.requestId.set(765873645L);
        allocationRejected.reason.set(RejectionReason.NOT_ENOUGH_SEATS);
        
        AllocationRejectedToJson translator = new AllocationRejectedToJson();
        
        JSONObject json = translator.toJson(allocationRejected);
        
        assertThat(json.get("accountId"), is((Object) allocationRejected.accountId.get()));
        assertThat(json.get("requestId"), is((Object) allocationRejected.requestId.get()));
        assertThat(json.get("reason"),    is((Object) allocationRejected.reason.get()));
        assertThat(json.get("type"),      is((Object) EventType.ALLOCATION_REJECTED.name()));
    }

}
