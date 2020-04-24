import api.ErrorMessage;
import api.StudentSearchApp;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class VKClientTest {

    private static StudentSearchApp app;

    @BeforeAll
    static void testStartUp() {
        app = new StudentSearchApp();
        app.startApp();
        app.userAuthorization();
    }

    @Test
    void testUserAuthorization() {
        var response = app.userAuthorization();
        assertEquals(ErrorMessage.SUCCESS, response);
    }

    @Test
    void testGetGroups() {
        var groupIds = app.getGroupIds();
        System.out.println(groupIds);
        Integer groupId = groupIds.get(0);
        var response = app.groupAuthorization(groupId);
        assertEquals(ErrorMessage.SUCCESS, response);
    }

}