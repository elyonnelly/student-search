package api;

import api.parse.Parser;
import api.parse.Query;
import api.search.Searcher;
import com.sun.net.httpserver.HttpServer;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.queries.groups.GroupsGetFilter;
import javafx.util.Pair;
import parser.DocParser;
import server.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class StudentSearchApp {
    VkAppSettings appSettings;
    VkApiClient vk;
    HttpRequestListener requestListener;

    private Searcher searcher;
    private Parser parser;
    private UserActor userActor;
    private List<Integer> groupIds;
    private Map<String, Integer> citiesId;
    private String userName;
    private List<AuthSubscriber> authSubscribers;

    public StudentSearchApp() throws IOException {
        appSettings = new VkAppSettings();
        TransportClient transportClient = HttpTransportClient.getInstance();
        vk = new VkApiClient(transportClient);
        requestListener = new HttpRequestListener();
        try {
            citiesId = loadCities();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка с подгрузкой ресурсов");
        }
        authSubscribers = new ArrayList<>();
        searcher = new Searcher(this);
        parser = new Parser(this);
        parser = new Parser(this);
    }

    public Searcher getSearcher() {
        return searcher;
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

    public List<Integer> getGroupIds() {
        return groupIds;
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
     * Запускает приложение на локальном хосту и порту
     * @throws IOException Ошибка при запуске Http-сервера
     */
    public void startApp() throws IOException {
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
     * Инициализирует объект userActor для работы с методами API VK
     * @throws ClientException Transport layer error
     * @throws ApiException Business logic error
     * @throws IOException Ошибка при открытии браузера для авторизации
     */
    public void userAuthorization() throws ClientException, ApiException, IOException {
        userActor = Authorization.createUserActor(this);
        groupIds = vk.groups().get(userActor).filter(GroupsGetFilter.ADMIN).execute().getItems();
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
    public void saveFile(List<List<Integer>> result, String listName, String path, boolean append) throws IOException {
        try( FileWriter listTitles = new FileWriter(new File(path + "listTitles.txt"), true)) {
            listTitles.write(listName + "\n");
        }
        var fileNames = buildNames("src/main/resources/data", listName);
        for (int i = 0; i < 3; i++) {
            try (FileWriter writer = new FileWriter(new File(fileNames.get(i)), append)) {
                for (var id : result.get(i)) {
                    writer.write(id.toString() + "\n");
                }
            }
        }
    }

    /**
     * отдает названия файлов по заголовку
     * @param path
     * @param title
     * @return
     */
    public static List<String> buildNames(String path, String title) {
        List<String> fileNames = new ArrayList<>();
        fileNames.add(path + "Winners" + title + ".txt");
        fileNames.add(path + "Prizewinners" + title + ".txt");
        fileNames.add(path + "Participant" + title + ".txt");
        return fileNames;
    }

    /**
     * Загружает информацию о списке
     * @param listName название списка
     * @return список с id найденных профилей
     * @throws IOException ошибка при работе с файлом
     */
    public List<List<Integer>> loadListFile(String listName) throws IOException {
        List<List<Integer>> result = new ArrayList<>();
        var fileNames = buildNames("src/main/resources/data", listName);
        for (int i = 0; i < 3; i++) {
            result.add(new ArrayList<>());
            try(BufferedReader reader = new BufferedReader(new FileReader(fileNames.get(i)))) {
                readIdFile(result.get(i), reader);
            }
        }
        return result;
    }

    private void readIdFile(List<Integer> list, BufferedReader reader) throws IOException {
        String line = reader.readLine();
        while(line != null) {
            list.add(Integer.valueOf(line));
            line = reader.readLine();
        }
    }

    public List<List<Integer>> fictitiousSearch(List<Query> participants, String listName, boolean append) throws IOException {
        var resultOfSearch = searcher.fictitiousSearch(participants);
        saveFile(resultOfSearch, listName, "src/main/resources/data/", append);
        saveFile(resultOfSearch, listName, "src/main/resources/data/", append);
        return resultOfSearch;
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
        var resultOfSearch = searcher.search(participants);
        saveFile(resultOfSearch, listName, "", append);
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
     * @param text список из содержимого страниц файла, каждая страница - список строк
     * @param fields поля в таблице
     * @param ranges диапазоны, в которых нужно парсить данные, для каждой страницы
     * @return список объектов Query, распарсенных из файла
     */
    public List<Query> handlePdfData(List<List<String>> text, List<String> fields, List<List<Pair<Integer, Integer>>> ranges) throws IOException {
        List<Query> queries = new ArrayList<>();
        for (int page = 0; page < ranges.size(); page++) {
            queries.addAll(parser.pdfParse(text, page, fields, ranges.get(page)));
        }
        return queries;
    }

    /**
     * Подгружает в память программы список городов для последующего поиска
     * @return таблица с указанием для каждого города его уникального id vk
     * @throws IOException ошибка при открытии файла с данными о городах
     */
    private Map<String, Integer> loadCities() throws IOException {
        Map<String, Integer> cities = new HashMap<>();
        File file = new File("src/main/resources/data/cities.txt");
        var path = file.getAbsolutePath();
        try (BufferedReader  reader = new BufferedReader(new FileReader(file))) {
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



