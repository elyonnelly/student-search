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
import java.util.Map;
import java.util.stream.Collectors;

class Authorization {

    static UserActor createUserActor(StudentSearchApp app) throws IOException, ClientException, ApiException {
        UserAuthResponse authResponse = null;
        try {
            authResponse = app.vk.oauth()
                    .userAuthorizationCodeFlow(app.appSettings.app_id, app.appSettings.client_secret,
                            app.appSettings.redirect_uri, getAuthCode(app, getUserAuthUri(app)))
                    .execute();
        }  catch (URISyntaxException e) {
            e.printStackTrace();
        }

        assert authResponse != null;
        return new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
    }

    static Map<Integer, String> getAuthGroupCodes(StudentSearchApp app) throws URISyntaxException, IOException, ClientException, ApiException {
        GroupAuthResponse authResponse = app.vk.oauth()
                .groupAuthorizationCodeFlow(app.appSettings.app_id, app.appSettings.client_secret,
                        app.appSettings.redirect_uri, getAuthCode(app, getGroupAuthUri(app)))
                .execute();
        return authResponse.getAccessTokens();
    }

    static GroupActor createGroupActor(Integer groupId, Map<Integer, String> authCodes) {
       return new GroupActor(groupId, authCodes.get(groupId));
    }

    private static String getAuthCode(StudentSearchApp app, URI AuthorizationUri) throws IOException {
        try {
            Desktop.getDesktop().browse(AuthorizationUri);
        } catch (IOException e) {
            throw new IOException("Не получается открыть браузер для авторизации");
        }

        try {
            while(app.requestListener.getParameters().size() == 0) {
                Thread.sleep(2000);
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        var parameter = app.requestListener.getParameters().get(0);
        if (parameter.getName().equals("code")) {
            String code = parameter.getValue();
            app.requestListener.getParameters().clear();
            return code;
        }
        else {
            throw new IOException("Не удается авторизоваться.");
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
        uri.addParameter("group_ids", app.getGroupIds().stream()
                                            .map(String::valueOf)
                                            .collect(Collectors.joining(",", "", "")));
        uri.addParameter("display", "page");
        uri.addParameter("scope", "manage");
        uri.addParameter("response_type", "code");
        uri.addParameter("v", "5.45");
        return uri.build();
    }
}
