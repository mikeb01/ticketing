package com.lmax.ticketing.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class WebListener implements Runnable
{
    private final URI baseUri;
    private final CountDownLatch latch;

    public WebListener(URI baseUri, CountDownLatch latch) throws URISyntaxException
    {
        this.baseUri = baseUri;
        this.latch = latch;
    }
    
    @Override
    public void run()
    {
        latch.countDown();
        try
        {
            String requestPattern = "response?account=12&version=%d";
            long version = 0;
            JSONParser parser = new JSONParser(JSONParser.MODE_RFC4627);
            while (!Thread.currentThread().isInterrupted())
            {
                URI requestUri = baseUri.resolve(String.format(requestPattern, version));
                System.out.println("Polling: " + requestUri);
                HttpURLConnection connection = (HttpURLConnection) requestUri.toURL().openConnection();
                connection.setRequestMethod("POST");
                
                connection.setDoOutput(true);
                connection.setDoInput(true);
                
                OutputStream out = connection.getOutputStream();
                out.write(new byte[0]);
                out.flush();
                out.close();
                
                InputStream in = connection.getInputStream();
                
                JSONArray json = (JSONArray) parser.parse(new InputStreamReader(in));
                
                in.close(); 
                connection.disconnect();
                
                
                for (int i = 0; i < json.size(); i++)
                {
                    JSONObject object = (JSONObject) json.get(i);
                    JSONValue.writeJSONString(object, System.out);
                    System.out.println();
                    
                    if (object.containsKey("version"))
                    {
                        long valueVersion = ((Number) object.get("version")).longValue();
                        version = Math.max(version, valueVersion);
                    }
                }
                
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
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException
    {
        CountDownLatch latch = new CountDownLatch(1);
        WebListener client = new WebListener(new URI("http://localhost:7070/"), latch);
        client.run();
    }
}
