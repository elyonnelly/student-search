import api.ErrorMessage;
import api.StudentSearchApp;
import api.query.Query;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.apache.http.HttpException;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class VKClientTest {

    private static StudentSearchApp app;

    @BeforeAll
    static void startUp() throws Exception {
        app = new StudentSearchApp();
        app.startApp();
        app.userAuthorization();
        //app.groupAuthorization(app.getGroupIds().get(0));
    }

    @Test
    List<Query> testHandleCsvData() throws ClientException, ApiException, IOException {
        String stringFields = "Адрес электронной почты\tСсылка на страницу ВК\tФамилия\tИмя\tОтчество\tДата рождения\tКод субъекта РФ\tГород\tШкола\tКласс";
        List<String> fields = Arrays.asList(stringFields.split("\t"));
        ClassLoader classLoader = getClass().getClassLoader();
        return app.handleCsvData(new File(Objects.requireNonNull(classLoader.getResource("test1.csv")).getFile()), fields);
    }

    @Test
    void testSearch() throws ClientException, ApiException, IOException, InterruptedException {
        var queries = testHandleCsvData();
        var result = app.search(queries, "testList");
        for (var list : result) {
            for (var user : list) {
                System.out.println(user);
            }
        }
    }

    @Test
    void testApiVK() throws ClientException, ApiException, IOException, InterruptedException {
        ClassLoader classLoader = getClass().getClassLoader();
        try (BufferedReader  reader = new BufferedReader(new FileReader("src/main/resources/russianTown.txt"));
             FileWriter writer = new FileWriter("src/main/resources/russianTownId", true)) {
            String city = reader.readLine();
            while (city != null) {
                var result = app.getVK().database().getCities(app.getUserActor(), 1).needAll(false).q(city).execute().getItems();
                if (result.size() > 0) {
                    writer.write(result.get(0).getTitle() + ";" + result.get(0).getId() + "\n");
                    System.out.println(result.get(0).getTitle());
                    Thread.sleep(340);
                }
                else {
                    result = app.getVK().database().getCities(app.getUserActor(), 1).needAll(false).q(city.split(" ")[0]).execute().getItems();
                    writer.write(result.get(0).getTitle() + ";" + result.get(0).getId() + "\n");
                    System.out.println(result.get(0).getTitle());
                    Thread.sleep(340);
                }
                city = reader.readLine();
            }
        }
    }

}