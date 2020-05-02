import api.ErrorMessage;
import api.StudentSearchApp;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.apache.http.HttpException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class VKClientTest {

    private static StudentSearchApp app;

    @BeforeAll
    static void testStartUp() throws Exception {
        app = new StudentSearchApp();
        app.startApp();
        app.userAuthorization();
        var groupIds = app.getGroupIds();
        System.out.println(groupIds);
        Integer groupId = groupIds.get(0);
        app.groupAuthorization(groupId);
    }

    @Test
    void testUserAuthorization() throws InterruptedException, HttpException, IOException, ApiException, URISyntaxException, ClientException {
        app.userAuthorization();
    }

    @Test
    void testGetGroups() throws Exception {
        var groupIds = app.getGroupIds();
        System.out.println(groupIds);
        Integer groupId = groupIds.get(0);
        app.groupAuthorization(groupId);
    }

    @Test
    void testSendMessage() throws FileNotFoundException {
        app.sendMessage(new File("input.txt"));
    }

}