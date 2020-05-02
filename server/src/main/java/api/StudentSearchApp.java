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

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class StudentSearchApp {
    VkAppSettings appSettings;
    VkApiClient vk;
    HttpRequestListener requestListener;

    private HttpServer server;
    private UserActor userActor;
    private GroupActor groupActor;
    private Map<Integer, String> groupAuthCodes;
    private List<Integer> groupIds;

    public StudentSearchApp() {
        appSettings = new VkAppSettings();
        TransportClient transportClient = HttpTransportClient.getInstance();
        vk = new VkApiClient(transportClient);
        requestListener = new HttpRequestListener();
    }

    /**
     * Start app on localhost:post
     */
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

    /**
     * Initialize userActor with params from config.properties.
     * Set group ids where userActor is admin
     * @return ErrorMessage
     */
    public void userAuthorization() throws InterruptedException, HttpException, IOException, ApiException, URISyntaxException, ClientException {
        userActor = Authorization.createUserActor(this);
        groupIds = vk.groups().get(userActor).filter(GroupsGetFilter.ADMIN).execute().getItems();
    }

    /**
     * Create authentication code for groups
     * @return
     */
    public void createGroupAuthCode() throws InterruptedException, HttpException, IOException, ApiException, URISyntaxException, ClientException {
        groupAuthCodes = Authorization.getAuthGroupCodes(this);
    }

    /**
     * Create groupActor for group with id == groupId
     * @param groupId group id
     * @return ErrorMessage
     * @throws Exception if authentication code for group did not set
     */
    public void groupAuthorization(Integer groupId) throws Exception {
        if (groupAuthCodes == null) {
            createGroupAuthCode();
        }
        groupActor = Authorization.createGroupActor(groupId, groupAuthCodes);
    }

    /**
     * Read users id from string stringIds
     * @return List of ids
     */
    private List<Integer> getIds(String stringIds) {
        var ids = new ArrayList<Integer>();
        var idsArray = stringIds.split("\n");
        for (var id : idsArray)
        {
            try {
                ids.add(Integer.valueOf(id));
            } catch (NumberFormatException ex) {
                throw new NumberFormatException("Id is incorrect!");
            }
        }
        return ids;
    }

    /**
     * Read users id from file fileIds
     * @return List of ids
     */
    public List<Integer> getIds(File fileIds) throws FileNotFoundException, NumberFormatException {
        var ids = new ArrayList<Integer>();
        try (Scanner sc = new Scanner(fileIds)) {
            while(sc.hasNextLine()) {
                ids.add(Integer.valueOf(sc.nextLine()));
                System.out.println(ids.get(ids.size() - 1));
            }
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("Id is incorrect!");
        } catch (FileNotFoundException ex) {
            System.out.println(fileIds.getAbsolutePath());
            throw new FileNotFoundException("File with id did not find");
        }
        return ids;
    }

    public void sendMessage(File fileIds) throws FileNotFoundException, NumberFormatException {
        List<Integer> ids;
        ids = getIds(fileIds);
        try {
            vk.messages().send(userActor).userIds(ids).message("Hello world!").execute();
        } catch (ApiException | ClientException e) {
            System.out.println(e.getMessage());
        }
    }

}



