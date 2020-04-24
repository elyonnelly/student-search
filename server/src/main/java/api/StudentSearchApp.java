package api;

import com.sun.net.httpserver.HttpServer;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.queries.groups.GroupsGetFilter;
import org.apache.http.HttpException;
import server.*;

import java.io.*;
import java.net.*;
import java.util.List;

public class StudentSearchApp {
    VkAppSettings appSettings;
    VkApiClient vk;
    HttpRequestListener requestListener;

    private HttpServer server;
    private UserActor userActor;
    private GroupActor groupActor;
    List<Integer> groupIds;

    public StudentSearchApp() {
        appSettings = new VkAppSettings();
        TransportClient transportClient = HttpTransportClient.getInstance();
        vk = new VkApiClient(transportClient);
        requestListener = new HttpRequestListener();
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

    public List<Integer> getGroupIds() {
        return groupIds;
    }

    public ErrorMessage userAuthorization() {
        try {
            userActor = Authorization.createUserActor(this);
            groupIds = vk.groups().get(userActor).filter(GroupsGetFilter.ADMIN).execute().getItems();
        } catch (IOException e) {
            e.printStackTrace();
            return ErrorMessage.BROWSER_ERROR;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return ErrorMessage.INCORRECT_URI;
        } catch (ApiException | ClientException | HttpException e) {
            e.printStackTrace();
            return ErrorMessage.AUTHORIZATION_ERROR;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return ErrorMessage.INTERRUPTED_ERROR;
        }
        return ErrorMessage.SUCCESS;
    }

    public ErrorMessage groupAuthorization(Integer groupId) {
        try {
            groupActor = Authorization.createGroupActor(this, groupId);
        } catch (IOException e) {
            e.printStackTrace();
            return ErrorMessage.BROWSER_ERROR;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return ErrorMessage.INCORRECT_URI;
        } catch (ApiException | ClientException | HttpException e) {
            e.printStackTrace();
            return ErrorMessage.AUTHORIZATION_ERROR;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return ErrorMessage.INTERRUPTED_ERROR;
        }
        return ErrorMessage.SUCCESS;
    }


}



