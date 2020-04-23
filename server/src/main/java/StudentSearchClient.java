import com.sun.net.httpserver.HttpServer;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.queries.groups.GroupsGetFilter;
import org.apache.http.HttpException;
import org.apache.http.client.utils.URIBuilder;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.List;

public class StudentSearchClient {
    VkAppSettings appSettings;
    VkApiClient vkClient;
    HttpRequestListener requestListener;
    HttpServer server;

    StudentSearchUserClient user;
    StudentSearchGroupClient group;

    public StudentSearchClient() {
        appSettings = new VkAppSettings();
        TransportClient transportClient = HttpTransportClient.getInstance();
        vkClient = new VkApiClient(transportClient);
        requestListener = new HttpRequestListener();
        user = new StudentSearchUserClient(this);
    }

    public void startApp() {
        var httpHandler = new HttpSimpleHandler();
        httpHandler.registerListener(requestListener);
        try {
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 80), 0);
            server.createContext("/", httpHandler);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}



