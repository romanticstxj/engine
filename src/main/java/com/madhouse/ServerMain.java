package com.madhouse;

import org.eclipse.jetty.server.handler.gzip.GzipHandler;

import com.madhouse.configuration.Bid;
import com.madhouse.configuration.WebApp;
import com.madhouse.resource.ResourceManager;
import com.madhouse.ssp.BidServlet;
import com.madhouse.ssp.ClickServlet;
import com.madhouse.ssp.ImpressionServlet;
import com.madhouse.util.httpserver.HttpServer;
import com.madhouse.util.httpserver.ServletHandler;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class ServerMain {
	public static void main(String[] args) {
		ServletHandler servletHandler = new ServletHandler(null);
		BidServlet bidServlet = new BidServlet();
		WebApp app = ResourceManager.getInstance().getPremiummad().getWebapp();
		servletHandler.addHandler(app.getImpression(), new ImpressionServlet());
		servletHandler.addHandler(app.getClick(), new ClickServlet());
		for (Bid bid : app.getBids()) {
			servletHandler.addHandler(bid.getPath(), bidServlet);
		}
		HttpServer httpServer = new HttpServer(servletHandler);
		if (app.getgZipOn()) {
			GzipHandler gzipHandler = new GzipHandler();
			gzipHandler.setMinGzipSize(1024);
			httpServer.insertHandler(gzipHandler);
		}

		if (httpServer.start(app.getPort())) {
			try {
				httpServer.join();
			} catch (Exception ex) {
				System.out.println(ex.toString());
			}
		}
	}
}
