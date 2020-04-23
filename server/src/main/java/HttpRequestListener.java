import org.apache.http.NameValuePair;

import java.util.List;

public class HttpRequestListener implements Listener {

    List<NameValuePair> parameters;

    @Override
    public void actionPerformed(List<NameValuePair> parameters) {
        this.parameters = parameters;
        System.out.println(parameters.size());
    }
}
