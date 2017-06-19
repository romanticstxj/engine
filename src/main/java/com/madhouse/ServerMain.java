package com.madhouse;

import com.madhouse.ssp.BidServlet;
import com.madhouse.ssp.ClickServlet;
import com.madhouse.ssp.ImpressionServlet;
import com.madhouse.util.httpserver.HttpServer;
import com.madhouse.util.httpserver.ServletHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

import java.util.UUID;


/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class ServerMain {
    public static void main(String[] args) {
        ServletHandler servletHandler = new ServletHandler(null);
        servletHandler.addHandler("/api/request", new BidServlet());
        servletHandler.addHandler("/api/impression", new ImpressionServlet());
        servletHandler.addHandler("/api/click", new ClickServlet());

        HttpServer httpServer = new HttpServer(servletHandler);
        boolean gzipOn = false;
        if (gzipOn) {
            GzipHandler gzipHandler = new GzipHandler();
            gzipHandler.setMinGzipSize(1024);
            httpServer.insertHandler(gzipHandler);
        }

        if (httpServer.start(8181)) {
            try {
                httpServer.join();
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
        }
    }
}
