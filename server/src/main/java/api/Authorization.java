package api;

import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.GroupAuthResponse;
import com.vk.api.sdk.objects.UserAuthResponse;
import org.apache.http.HttpException;
import org.apache.http.client.utils.URIBuilder;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

class Authorization {

    static UserActor createUserActor(StudentSearchApp app) throws URISyntaxException, InterruptedException, IOException, HttpException, ClientException, ApiException {
        UserAuthResponse authResponse = app.vk.oauth()
                .userAuthorizationCodeFlow(app.appSettings.app_id, app.appSettings.client_secret,
                        app.appSettings.redirect_uri, getAuthCode(app, getUserAuthUri(app)))
                .execute();
        return new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
    }

    static GroupActor createGroupActor(StudentSearchApp app, Integer groupId) throws URISyntaxException, InterruptedException, IOException, HttpException, ClientException, ApiException {
        GroupAuthResponse authResponse = app.vk.oauth()
                .groupAuthorizationCodeFlow(app.appSettings.app_id, app.appSettings.client_secret,
                                            app.appSettings.redirect_uri, getAuthCode(app, getGroupAuthUri(app)))
                .execute();

       return new GroupActor(groupId, authResponse.getAccessTokens().get(groupId));
    }

    private static String getAuthCode(StudentSearchApp app, URI AuthorizationUri) throws IOException, InterruptedException, HttpException {
        try {
            Desktop.getDesktop().browse(AuthorizationUri);
        } catch (IOException e) {
            throw new IOException("Unable to open browser for authorization");
        }

        //здесь нужно ожидание запроса
        while(app.requestListener.getParameters().size() == 0) {
            Thread.sleep(2000);
        }
        var parameter = app.requestListener.getParameters().get(0);
        if (parameter.getName().equals("code")) {
            String code = parameter.getValue();
            app.requestListener.getParameters().clear();
            return code;
        }
        else {
            throw new HttpException("code parameter not received");
        }
    }

    private static URI getUserAuthUri(StudentSearchApp app) throws URISyntaxException {
        String authorizationUri = "https://oauth.vk.com/authorize";
        URIBuilder uri = new URIBuilder(authorizationUri);
        uri.addParameter("client_id", app.appSettings.app_id.toString());
        uri.addParameter("display", "page");
        uri.addParameter("redirect_uri", app.appSettings.redirect_uri);
        uri.addParameter("scope", "groups");
        uri.addParameter("response_type", "code");
        uri.addParameter("v", "5.45");
        return uri.build();
    }

    private static URI getGroupAuthUri(StudentSearchApp app) throws URISyntaxException {
        String authorizationUri = "https://oauth.vk.com/authorize";
        URIBuilder uri = new URIBuilder(authorizationUri);
        uri.addParameter("client_id", app.appSettings.app_id.toString());
        uri.addParameter("redirect_uri", app.appSettings.redirect_uri);
        uri.addParameter("group_ids", app.groupIds.stream()
                                            .map(String::valueOf)
                                            .collect(Collectors.joining(",", "", "")));
        uri.addParameter("display", "page");
        uri.addParameter("scope", "manage,messages");
        uri.addParameter("response_type", "code");
        uri.addParameter("v", "5.45");
        return uri.build();
    }
}
