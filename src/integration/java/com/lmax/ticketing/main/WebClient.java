package com.lmax.ticketing.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

public class WebClient implements Runnable
{
    private final URI url;
    private final CountDownLatch latch;

    public WebClient(URI url, CountDownLatch latch) throws URISyntaxException
    {
        this.url = url;
        this.latch = latch;
    }
    
    @Override
    public void run()
    {
        latch.countDown();
        try
        {
            while (!Thread.currentThread().isInterrupted())
            {
                System.out.println("Polling: " + url);
                HttpURLConnection connection = (HttpURLConnection) url.toURL().openConnection();
                connection.setRequestMethod("POST");
                
                connection.setDoOutput(true);
                connection.setDoInput(true);
                
                OutputStream out = connection.getOutputStream();
                out.write(new byte[0]);
                out.flush();
                out.close();
                
                InputStream in = connection.getInputStream();
                System.out.println(connection.getResponseCode());
                
                int numRead = 0;
                byte[] data = new byte[1024];
                while ((numRead = in.read(data)) != -1)
                {
                    System.out.write(data, 0, numRead);
                }
                System.out.println();
                
                in.close(); 
                connection.disconnect();
            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException
    {
        Executor executor = Executors.newCachedThreadPool();
        CountDownLatch latch = new CountDownLatch(1);
        WebClient client = new WebClient(new URI("http://localhost:7070/response?account=12"), latch);
        executor.execute(client);
        
        latch.await();
        
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
