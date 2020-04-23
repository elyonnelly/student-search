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
    String userCode;
    HttpRequestListener requestListener;
    UserActor userActor;
    GroupActor groupActor;
    HttpServer server;
    List<Integer> groups;

    public StudentSearchClient() {
        appSettings = new VkAppSettings();
        TransportClient transportClient = HttpTransportClient.getInstance();
        vkClient = new VkApiClient(transportClient);
        requestListener = new HttpRequestListener();
    }

    public void doAuthorization() throws ClientException, ApiException, URISyntaxException, IOException, HttpException, InterruptedException {
        setAccessToken();
        setUserActor();
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

    public void overviewGroups() throws ClientException, ApiException {
        groups = vkClient.groups().get(userActor).filter(GroupsGetFilter.ADMIN).execute().getItems();
    }

    private void setAccessToken() throws HttpException, IOException, InterruptedException, URISyntaxException, ClientException, ApiException {
        getUserCode();

        UserAuthResponse authResponse = vkClient.oauth()
                .userAuthorizationCodeFlow(Integer.valueOf(appSettings.app_id), appSettings.client_secret, appSettings.redirect_uri, userCode)
                .execute();
        appSettings.access_token = authResponse.getAccessToken();
        appSettings.user_id = authResponse.getUserId();
    }

    public void setGroupActor() {
        GroupActor actor = new GroupActor(appSettings.group_id, appSettings.access_token);
    }

    public void setUserActor() throws ClientException, ApiException, URISyntaxException, IOException, HttpException, InterruptedException {
        userActor = new UserActor(appSettings.user_id, appSettings.access_token);
    }

    private void getUserCode() throws URISyntaxException, IOException, HttpException, InterruptedException {
        Desktop.getDesktop().browse(getAuthorizationUri());

        //здесь нужно врубить ожидание ответа от сервера крч
        while(requestListener.parameters == null) {
             Thread.sleep(2000);
        }
        System.out.println("success");
        var parameter = requestListener.parameters.get(0);
        if (parameter.getName().equals("code")) {
            userCode = parameter.getValue();
        }
        else {
            throw new HttpException("code parameter not received");
        }
    }

    private URI getAuthorizationUri() throws URISyntaxException {
        String authorizationUri = "https://oauth.vk.com/authorize";
        URIBuilder uri = new URIBuilder(authorizationUri);
        uri.addParameter("client_id", appSettings.app_id);
        uri.addParameter("display", "page");
        uri.addParameter("redirect_uri", appSettings.redirect_uri);
        uri.addParameter("scope", "groups");
        uri.addParameter("response_type", "code");
        uri.addParameter("v", "5.45");
        return uri.build();
    }
}



