package server;

import org.apache.http.NameValuePair;

import java.util.List;

public interface Listener {
    void actionPerformed(List<NameValuePair> parameters);
}
