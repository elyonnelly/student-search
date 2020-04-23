import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VKClientTest {
    @Test
    void test1() throws FileNotFoundException {
        VKClient client = new VKClient();
        assertEquals("7425201", client.settings.app_id);
    }

    @Test
    void testStartUp() throws FileNotFoundException, URISyntaxException {
        VKClient client = new VKClient();
        client.startUp();
    }
}