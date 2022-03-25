package fi.invian.codingassignment.app;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class BackendApplication {
    public static void main(String[] args) throws Exception {
        URI baseUri = UriBuilder.fromUri("http://127.0.0.1/").port(8080).build();
        Server server = JettyHttpContainerFactory.createServer(baseUri, false);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/*");
        ServletContainer jersey = new ServletContainer(new ResourceConfig() {{
            packages("fi.invian.codingassignment.rest");
        }});
        ServletHolder holder = new ServletHolder(jersey);
        context.addServlet(holder, "/*");
        server.setHandler(context);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseConnection.closeDataSource();
            try {
                server.stop();
            } catch (Exception e) {
                // ignored
            }
        }));
        server.start();
    }
}
