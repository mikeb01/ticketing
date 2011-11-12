package com.lmax.ticketing.domain;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URI;

import org.junit.Test;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

public class WebTest
{
    @Test
    public void purchaseTicket() throws Exception
    {
        JSONObject json = new JSONObject();
        json.put("accountId", 12);
        json.put("requestId", System.currentTimeMillis());
        json.put("numSeats", 2);
        json.put("concertId", 321);
        json.put("sectionId", 879);
        
        URI requestUri = new URI("http://localhost:7070/request");
        HttpURLConnection cn = (HttpURLConnection) requestUri.toURL().openConnection();
        
        cn.setDoInput(true);
        cn.setDoOutput(true);
        
        OutputStream out = cn.getOutputStream();
        Writer writer = new OutputStreamWriter(out);
        
        JSONValue.writeJSONString(json, System.out);
        System.out.println();
        JSONValue.writeJSONString(json, writer);
        writer.flush();
        out.flush();
        out.close();
        
        int responseCode = cn.getResponseCode();
        
        System.out.println("Response code from send: " + responseCode);        
    }
}
