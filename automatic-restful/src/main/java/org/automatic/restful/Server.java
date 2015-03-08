package org.automatic.restful;

import java.io.IOException;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
	private static final Logger logger = LoggerFactory.getLogger(Server.class);
	public static final String BASE_URI = "http://localhost:8080/myapp";
	
	public static HttpServer startServer() throws IOException {
		logger.info("Initiliazing Grizzly server..");
		// set REST services packages
		final ResourceConfig rc = new ResourceConfig().packages(
				"org.automatic.restful");

		// instantiate server
		final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
				URI.create(BASE_URI), rc);
		StaticHttpHandler staticHttpHandler = new StaticHttpHandler("static/");
		server.getServerConfiguration().addHttpHandler(staticHttpHandler,
				"/static");
		// register shutdown hook
//		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//			@Override
//			public void run() {
//				logger.info("Stopping server..");
//				server.stop();
//			}
//		}, "shutdownHook"));
	
		// run
		try {
			server.start();
			logger.info("Press CTRL^C to exit..");
			Thread.currentThread().join();
		} catch (Exception e) {
			logger.error(
					"There was an error while starting Grizzly HTTP server.", e);
		}
		return server;
	}

}
