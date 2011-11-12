package com.lmax.ticketing.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.JSONParser;

import com.lmax.disruptor.collections.Histogram;

public class PerformanceClient implements Runnable
{
    private final long accountId;
    private final long iterations;
    private final Histogram histogram = new Histogram(createBounds(1, 100000));
    private final long concertId;
    private final long sectionId;

    public PerformanceClient(long accountId, long iterations, long concertId, long sectionId) throws URISyntaxException
    {
        this.accountId = accountId;
        this.iterations = iterations;
        this.concertId = concertId;
        this.sectionId = sectionId;
    }

    @Override
    public void run()
    {
        try
        {
            String baseUrl = "http://localhost:7070/response?account=" + accountId + "&version=";
            URL requestUri = new URL("http://localhost:7070/request");
            long version = -1;
            
            JSONObject json = new JSONObject();
            json.put("accountId", accountId);
            json.put("numSeats", 2);
            json.put("concertId", concertId);
            json.put("sectionId", sectionId);
            JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
            
            for (int x = 0; x < iterations; x++)
            {
                long t0 = System.nanoTime();
                
                long requestId = System.currentTimeMillis();
                json.put("requestId", requestId);
                
                HttpURLConnection cn = (HttpURLConnection) requestUri.openConnection();
                cn.setRequestMethod("POST");

                cn.setDoOutput(true);
                
                OutputStream out = cn.getOutputStream();
                Writer writer = new OutputStreamWriter(out);
                
                JSONValue.writeJSONString(json, writer);
                writer.flush();
                out.close();
                
                int code = cn.getResponseCode();
                if (200 != code)
                {
                    System.err.println("Invalid response code" + code);
                    return;
                }
                
                boolean foundResponse = false;
                
                while (!foundResponse)
                {
                    URL responseUrl = new URL(baseUrl + version);
                    
                    cn = (HttpURLConnection) responseUrl.openConnection();
                    
                    InputStream in = cn.getInputStream();
                    
                    JSONArray response = (JSONArray) parser.parse(new InputStreamReader(in));
                    in.close(); 
                    
                    for (int i = 0, n = response.size(); i < n; i ++)
                    {
                        JSONObject event = (JSONObject) response.get(i);
                        Number versionNumber = (Number) event.get("version");
                        if (null != versionNumber)
                        {
                            version = versionNumber.longValue();
                        }
                        Number requestIdNumber = (Number) event.get("requestId");
                        foundResponse |= (null != requestIdNumber) && requestId == requestIdNumber.longValue();
                    }
                }
                
                long t1 = System.nanoTime();
                
                long timeUs = (t1-t0)/1000;
                histogram.addObservation(timeUs);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException
    {
        System.out.println("Warm-up");
        PerformanceClient client = new PerformanceClient(100, 20000, 1, 1);
        client.run();
        System.out.println(print(client.histogram));
        runRealPass();
    }

    private static void runRealPass() throws URISyntaxException, InterruptedException
    {
        System.out.println("Real run");
        int threadCount = 10;
        long concertIds = 2;
        long sectionIds = 8;
        
        Thread[] ts = new Thread[threadCount];
        PerformanceClient[] cs = new PerformanceClient[threadCount];
        
        for (int i = 0; i < threadCount; i++)
        {
            long concertId = (i % concertIds) + 1;
            long sectionId = (i % sectionIds) + 1;
            long accountId = i;
            cs[i] = new PerformanceClient(accountId, 20000, concertId, sectionId);
            ts[i] = new Thread(cs[i]);
            ts[i].start();
        }
        
        for (Thread t : ts)
        {
            t.join();
        }
        
        Histogram results = new Histogram(createBounds(1, 100000));
        
        for (PerformanceClient c : cs)
        {
            results.addObservations(c.histogram);
        }
        
        System.out.println(print(results));
    }
    
    public static String print(Histogram h)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Histogram{");

        sb.append("min=").append(h.getMin()).append(", ");
        sb.append("max=").append(h.getMax()).append(", ");
        sb.append("mean=").append(h.getMean()).append(", ");
        sb.append("99%=").append(h.getTwoNinesUpperBound()).append(", ");
        sb.append("99.99%=").append(h.getFourNinesUpperBound()).append("}");
        
        return sb.toString();
    }
    
    private static long[] createBounds(int i, int j)
    {
        long[] bounds = new long[(j - i) + 1];
        for (int x = 0; i <= j; i++, x++)
        {
            bounds[x] = i;
        }
        
        return bounds;
    }
}
