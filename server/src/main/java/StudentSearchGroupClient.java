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

public class StudentSearchGroupClient {
    GroupActor groupActor;
    StudentSearchClient ssClient;

    public StudentSearchGroupClient(StudentSearchClient client) {
        ssClient = client;
    }

    public void setGroupActor() {
        //GroupActor actor = new GroupActor(ssClient.appSettings.group_id, ssClient.appSettings.access_token);
    }

}



