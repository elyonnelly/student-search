import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.client.utils.URIBuilder;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class VKClient {
    ClientSettings settings;

    public VKClient() throws FileNotFoundException {
        settings = new ClientSettings();
    }

    public void startUp() throws URISyntaxException {


        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 80), 0);
            server.createContext("/test", new  MyHttpHandler());
            server.setExecutor(threadPoolExecutor);
            server.start();

            URIBuilder uri = new URIBuilder("https://oauth.vk.com/authorize");
            uri.addParameter("client_id", settings.app_id);
            uri.addParameter("display", "page");
            uri.addParameter("redirect_uri", "http://127.0.0.1:80");
            uri.addParameter("scope", "groups");
            uri.addParameter("response_type", "code");
            uri.addParameter("v", "5.45");
            System.out.println(uri.build());
            try {
                Desktop.getDesktop().browse(uri.build());
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }

            try(Socket clientSocket = server.accept();
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream()))
            {
                System.out.println("connected!");
               /* while(true) {
                    var request = in.readUTF();
                    System.out.println("server: " + request);
                }*/
            }catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyHttpHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String requestParamValue=null;

            if("GET".equals(httpExchange.getRequestMethod())) {
                requestParamValue = handleGetRequest(httpExchange);
            }

            handleResponse(httpExchange,requestParamValue);
        }

        private String handleGetRequest(HttpExchange httpExchange) {
            return httpExchange.
                    getRequestURI()
                    .toString()
                    .split("\\?")[1]
                    .split("=")[1];
        }

        private void handleResponse(HttpExchange httpExchange, String requestParamValue)  throws  IOException {
            OutputStream outputStream = httpExchange.getResponseBody();
            StringBuilder htmlBuilder = new StringBuilder();

            htmlBuilder.append("").
                    append("").
                    append("<h1>").
                    append("Hello ")
                    .append(requestParamValue)
                    .append("</h1>")
                    .append("")
                    .append("");

            // encode HTML content
            String htmlResponse = StringEscapeUtils.escapeHtml4(htmlBuilder.toString());

            // this line is a must
            httpExchange.sendResponseHeaders(200, htmlResponse.length());

            outputStream.write(htmlResponse.getBytes());
            outputStream.flush();
            outputStream.close();
        }
    }
}



