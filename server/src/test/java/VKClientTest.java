import api.StudentSearchApp;
import api.parse.Query;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import javafx.util.Pair;

import java.io.*;
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
    void testHandlePdfData() throws IOException, ClientException, ApiException {
        app = new StudentSearchApp();
        String testData = "src/main/resources/test0.pdf";
        var parsedText = app.parsePdfByLine(new File(testData));

        List<List<Pair<Integer, Integer>>> ranges = new ArrayList<>();
        Scanner in = new Scanner(System.in);
        for (var page : parsedText) {
            for (int i = 0; i < page.size(); i++) {
                System.out.println(i + " " + page.get(i));
            }
            /*System.out.println("Сколько диапазонов необходимо выбрать в данных?");
            int numberOfRanges = in.nextInt();
            System.out.println("Введите диапазоны");
            ranges.add(new ArrayList<>());
            for (int i = 0; i < numberOfRanges; i++) {
                int s = in.nextInt();
                int f = in.nextInt();
                Pair<Integer, Integer> range = new Pair<>(s, f);
                ranges.get(ranges.size() - 1).add(range);
            }*/
        }

        String stringFields = "Номер ФИО Город Класс 1тур 2тур Апелляция Результат СтатусУчастника";
        List<String> fields = Arrays.asList(stringFields.split(" "));

        //List<Query> handleResult = app.handlePdfData(parsedText, fields, ranges);

    }

    @Test
    List<Query> testHandleCsvData() throws ClientException, ApiException, IOException {
        String stringFields = "Адрес электронной почты\tСсылка на страницу ВК\tФамилия\tИмя\tОтчество\tДата рождения\tКод субъекта РФ\tГород\tШкола\tКласс";
        List<String> fields = Arrays.asList(stringFields.split("\t"));
        ClassLoader classLoader = getClass().getClassLoader();
        return app.handleCsvData(new File(Objects.requireNonNull(classLoader.getResource("test1.csv")).getFile()), fields);
    }

    @Test
    void testSearchUsers() throws ClientException, ApiException, IOException, InterruptedException {
        var queries = testHandleCsvData();
        var result = app.search(queries, "testList", true);
        for (var list : result) {
            for (var user : list) {
                System.out.println(user);
            }
        }
    }

    @Test
    void findCities() throws ClientException, ApiException, IOException, InterruptedException {
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