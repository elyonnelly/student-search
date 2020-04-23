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

public class StudentSearchUserClient {

    StudentSearchClient ssClient;
    UserActor userActor;
    String userCode;
    List<Integer> groups;

    public StudentSearchUserClient(StudentSearchClient client) {
        ssClient = client;
    }


    public void overviewGroups() throws ClientException, ApiException {
        groups = ssClient.vkClient.groups().get(userActor).filter(GroupsGetFilter.ADMIN).execute().getItems();
    }

    public void doAuthorization() throws ClientException, ApiException, URISyntaxException, IOException, HttpException, InterruptedException {
        setAccessToken();
        setUserActor();
    }

    private void setAccessToken() throws HttpException, IOException, InterruptedException, URISyntaxException, ClientException, ApiException {
        getUserCode();

        UserAuthResponse authResponse = ssClient.vkClient.oauth()
                .userAuthorizationCodeFlow(Integer.valueOf(ssClient.appSettings.app_id),
                        ssClient.appSettings.client_secret, ssClient.appSettings.redirect_uri, userCode)
                .execute();
        ssClient.appSettings.access_token = authResponse.getAccessToken();
        ssClient.appSettings.user_id = authResponse.getUserId();
    }

    public void setUserActor() throws ClientException, ApiException, URISyntaxException, IOException, HttpException, InterruptedException {
        userActor = new UserActor(ssClient.appSettings.user_id, ssClient.appSettings.access_token);
    }

    private void getUserCode() throws URISyntaxException, IOException, HttpException, InterruptedException {
        Desktop.getDesktop().browse(getAuthorizationUri());

        //здесь нужно врубить ожидание ответа от сервера крч
        while(ssClient.requestListener.parameters == null) {
            Thread.sleep(2000);
        }
        System.out.println("success");
        var parameter = ssClient.requestListener.parameters.get(0);
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
        uri.addParameter("client_id", ssClient.appSettings.app_id);
        uri.addParameter("display", "page");
        uri.addParameter("redirect_uri", ssClient.appSettings.redirect_uri);
        uri.addParameter("scope", "groups");
        uri.addParameter("response_type", "code");
        uri.addParameter("v", "5.45");
        return uri.build();
    }
}



