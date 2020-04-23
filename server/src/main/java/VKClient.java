import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.logging.Handler;

public class VKClient {
    ClientSettings settings;

    public VKClient() throws FileNotFoundException {
        settings = new ClientSettings();
    }

    private class MyHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println(exchange.getRequestURI().getQuery());
            var parameters = URLEncodedUtils.parse(exchange.getRequestURI(), String.valueOf(Charset.defaultCharset()));
            for (var p : parameters) {
                System.out.println(p.getName() + " " + p.getValue());
            }
        }
    }

    public void getCode() throws URISyntaxException {
        String authorizationUri = "https://oauth.vk.com/authorize";
        URIBuilder uri = new URIBuilder(authorizationUri);
        uri.addParameter("client_id", settings.app_id);
        uri.addParameter("display", "page");
        uri.addParameter("redirect_uri", settings.redirect_uri);
        uri.addParameter("scope", "groups");
        uri.addParameter("response_type", "code");
        uri.addParameter("v", "5.45");
        try {
            Desktop.getDesktop().browse(uri.build());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void startUp() throws URISyntaxException {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 80), 0);
            server.createContext("/", new MyHandler());
            server.start();
            getCode();
            while (true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}



