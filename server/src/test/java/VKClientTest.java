import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.apache.http.HttpException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class VKClientTest {

    @Test
    void testStartUp() throws IOException, URISyntaxException, HttpException, ApiException, ClientException, InterruptedException {
        StudentSearchClient client = new StudentSearchClient();
        client.startApp();
        client.user.doAuthorization();
        assertNotNull(client.user.userActor);
    }

    @Test
    void testGetGroups() throws InterruptedException, HttpException, IOException, ApiException, URISyntaxException, ClientException {
        StudentSearchClient client = new StudentSearchClient();
        client.startApp();
        client.user.setUserActor();
        client.user.overviewGroups();
        assertEquals(client.appSettings.group_id, client.user.groups.get(0));
    }
}