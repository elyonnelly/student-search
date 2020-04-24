package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.http.client.utils.URLEncodedUtils;

import java.nio.charset.Charset;

public class HttpSimpleHandler implements HttpHandler {

    Listener listener;

    public void registerListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void handle(HttpExchange exchange) {
        var parameters = URLEncodedUtils.parse(exchange.getRequestURI(), String.valueOf(Charset.defaultCharset()));

        listener.actionPerformed(parameters);
    }

}
