package server;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;

public class HttpRequestListener implements Listener {

    List<NameValuePair> parameters;

    public HttpRequestListener() {
        parameters = new ArrayList<>();
    }

    @Override
    public void actionPerformed(List<NameValuePair> parameters) {
        this.parameters = parameters;
    }

    public List<NameValuePair> getParameters() {
        return parameters;
    }
}
