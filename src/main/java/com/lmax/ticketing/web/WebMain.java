package com.lmax.ticketing.web;

import java.util.logging.Handler;
import java.util.logging.Logger;

import org.eclipse.persistence.logging.LogFormatter;

import com.caucho.resin.HttpEmbed;
import com.caucho.resin.ResinEmbed;
import com.caucho.resin.WebAppEmbed;

public class WebMain
{
    public static void main(String[] args)
    {
        Handler[] handlers = Logger.getLogger("").getHandlers();
        for (Handler handler : handlers)
        {
            handler.setFormatter(new LogFormatter());
        }
        
        ResinEmbed resin = new ResinEmbed();

        HttpEmbed http = new HttpEmbed(7070);
        resin.addPort(http);

        WebAppEmbed webapp = new WebAppEmbed("/", "src/main/web");
        resin.addWebApp(webapp);

        resin.start();
        resin.join();
    }
}
