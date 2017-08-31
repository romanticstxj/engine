package com.madhouse;

import com.madhouse.cache.CacheManager;
import com.madhouse.util.Utility;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

import com.madhouse.configuration.Bid;
import com.madhouse.configuration.WebApp;
import com.madhouse.resource.ResourceManager;
import com.madhouse.ssp.BidServlet;
import com.madhouse.ssp.ClickServlet;
import com.madhouse.ssp.ImpressionServlet;
import com.madhouse.util.httpserver.HttpServer;
import com.madhouse.util.httpserver.ServletHandler;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class ServerMain {
	public static void main(String[] args) {
		//resource init
		ResourceManager.getInstance().init();
		//cache init
		CacheManager.getInstance().init();

		ServletHandler servletHandler = new ServletHandler(null);
		WebApp webApp = ResourceManager.getInstance().getConfiguration().getWebapp();
		servletHandler.addHandler(webApp.getImpression(), new ImpressionServlet());
		servletHandler.addHandler(webApp.getClick(), new ClickServlet());

		BidServlet bidServlet = new BidServlet();
		for (Bid bid : webApp.getBids()) {
			servletHandler.addHandler(bid.getPath(), bidServlet);
		}

		HttpServer httpServer = new HttpServer(webApp.getMinIdle(), webApp.getMaxTotal(), webApp.getMaxIdle(), servletHandler);
		if (webApp.getgZipOn()) {
			GzipHandler gzipHandler = new GzipHandler();
			gzipHandler.setMinGzipSize(1024);
			gzipHandler.setCheckGzExists(true);
			httpServer.insertHandler(gzipHandler);
		}

		if (httpServer.start(webApp.getPort())) {
			try {
				httpServer.join();
			} catch (Exception ex) {
				System.out.println(ex.toString());
			}
		}
	}
}
