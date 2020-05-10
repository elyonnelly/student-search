package api;

import api.parse.Parser;
import api.parse.Query;
import api.search.Requester;
import com.sun.net.httpserver.HttpServer;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.apache.http.impl.io.IdentityOutputStream;
import parser.DocParser;
import server.HttpRequestListener;
import server.HttpSimpleHandler;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentSearchApp {
    VkAppSettings appSettings;
    VkApiClient vk;
    HttpRequestListener requestListener;

    private Requester requester;
    private Parser parser;
    private UserActor userActor;
    private Map<String, Integer> citiesId;
    private String userName;
    private List<AuthSubscriber> authSubscribers;

    public StudentSearchApp() throws IOException {
        appSettings = new VkAppSettings(null);
        TransportClient transportClient = HttpTransportClient.getInstance();
        vk = new VkApiClient(transportClient);
        requestListener = new HttpRequestListener();
        try {
            citiesId = loadCities(null);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка с подгрузкой ресурсов");
        }
        authSubscribers = new ArrayList<>();
        requester = new Requester(this);
        parser = new Parser(this);
        startApp();
    }

    public StudentSearchApp(InputStream cities, InputStream properties) throws IOException {
        appSettings = new VkAppSettings(properties);
        TransportClient transportClient = HttpTransportClient.getInstance();
        vk = new VkApiClient(transportClient);
        requestListener = new HttpRequestListener();
        try {
            citiesId = loadCities(cities);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка с подгрузкой ресурсов");
        }
        authSubscribers = new ArrayList<>();
        requester = new Requester(this);
        parser = new Parser(this);
        startApp();
    }

    public Requester getRequester() {
        return requester;
    }

    public Map<String, Integer> getCitiesId() {
        return citiesId;
    }

    public VkApiClient getVK() {
        return vk;
    }

    public UserActor getUserActor() {
        return userActor;
    }

    public String getUserName() {
        return userName;
    }

    public void subscribe(AuthSubscriber s) {
        authSubscribers.add(s);
    }

    public void unsubscribe(AuthSubscriber s) {
        authSubscribers.remove(s);
    }

    private void notifyAuthSubscribers() {
        for (int i = 0; i < authSubscribers.size(); i++) {
            authSubscribers.get(i).update();
        }
    }

    /**
     * Инициализирует объект userActor для работы с методами API VK
     * @throws ClientException Transport layer error
     * @throws ApiException Business logic error
     * @throws IOException Ошибка при открытии браузера для авторизации
     */
    public void userAuthorization() throws ClientException, ApiException, IOException {
        userActor = Authorization.createUserActor(this);
        var userInfo = vk.users().get(userActor).userIds(userActor.getId().toString()).execute();
        userName = userInfo.get(0).getFirstName() + " " + userInfo.get(0).getLastName();
        notifyAuthSubscribers();
    }

    /**
     * Сохраняет в файлы данные о найденных пользователях,
     * победители сохраняются в файл Winners{listName}.txt, аналогично Prizewinners и Participant.
     * @param result список с найденными id
     * @param listName название списка
     * @throws IOException ошибка при сохранении файлов
     */
    public static void saveFile(List<List<Integer>> result, String listName, String path, boolean append, boolean foRUser) throws IOException {
        System.out.println("saving...");
        File fileTitles = new File("listTitles.txt");
        if (!foRUser) {
            try( FileWriter titles = new FileWriter(fileTitles, true)) {
                titles.write(listName + "\n");
            }
        }
        var fileNames = buildNames(listName);
        for (int i = 0; i < 3; i++) {
            try (FileWriter writer = new FileWriter(fileNames.get(i), append)) {
                for (var id : result.get(i)) {
                    writer.write(id.toString() + "\n");
                }
            }
        }
    }

    /**
     * отдает названия файлов по заголовку
     * @param title
     * @return
     */
    public static List<String> buildNames(String title) {
        List<String> fileNames = new ArrayList<>();
        fileNames.add("Winners_" + title + ".txt");
        fileNames.add("Prizewinners_" + title + ".txt");
        fileNames.add("Participant_" + title + ".txt");
        return fileNames;
    }

    /**
     * Возвращает список найденных id
     * @param participants список пользователей для поиска
     * @param listName название списка
     * @return resultOfSearch - список найденных id, где resultOfSearch[0] - список победителей, [2] - призеров, [3] - участников
     * Если статус участника не указан, все добавляются в список участников
     * @throws IOException Ошибка записи результатов поиска в файл
     */
    public List<List<Integer>> search(List<Query> participants, String listName, boolean append) throws IOException {
        var resultOfSearch = requester.search(participants);
        System.out.println("save");
        saveFile(resultOfSearch, listName, "data/", append, false);
        return resultOfSearch;
    }

    /**
     * Возвращает распарсенный текст из csv-файла в виде списка Query
     * @param file файл с данными
     * @param fields поля в файле
     * @return список объектов Query, распарсенных из файла
     * @throws IOException Ошибка при чтении из файла
     */
    public List<Query> handleCsvData(File file, List<String> fields) throws IOException {
        return parser.csvParse(file, fields, true);
    }

    /**Возвращает текст из pdf построчно
     * @param source файл с данными
     * @return список из содержимого страниц файла, каждая страница - список строк
     * @throws IOException ошибка при чтении из файла
     */
    public List<List<String>> parsePdfByLine(File source) throws IOException {
        return DocParser.parse(source);
    }

    /**
     * Возвращает распарсенный построчный текст из pdf-файла в виде списка Query
     * @param lines список из содержимого страниц файла
     * @param fields поля в таблице
     * @return список объектов Query, распарсенных из файла
     */
    public List<Query> handlePdfData(List<String> lines, List<String> fields) throws IOException {
        return parser.pdfParse(lines, fields);
    }

    /**
     * Запускает приложение на локальном хосту и порту
     * @throws IOException Ошибка при запуске Http-сервера
     */
    private void startApp() throws IOException {
        var httpHandler = new HttpSimpleHandler();
        httpHandler.registerListener(requestListener);
        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 80), 0);
        } catch (IOException e) {
            throw new IOException("Не удается запустить приложение.");
        }
        server.createContext("/", httpHandler);
        server.start();
    }


    /**
     * Подгружает в память программы список городов для последующего поиска
     * @return таблица с указанием для каждого города его уникального id vk
     * @throws IOException ошибка при открытии файла с данными о городах
     */
    private Map<String, Integer> loadCities(InputStream file) throws IOException {
        Map<String, Integer> cities = new HashMap<>();
        try (BufferedReader  reader = new BufferedReader(new InputStreamReader(file))) {
            String city = reader.readLine();
            while (city != null) {
                var split = city.split(";");
                cities.put(split[0].toLowerCase(), Integer.valueOf(split[1]));
                city = reader.readLine();
            }
        }
        return cities;
    }
}



