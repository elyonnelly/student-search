package api;

import api.query.Parser;
import api.query.Query;
import api.search.Searcher;
import com.sun.net.httpserver.HttpServer;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.queries.groups.GroupsGetFilter;
import javafx.util.Pair;
import org.apache.http.HttpException;
import parser.DocParser;
import server.*;

import java.io.*;
import java.net.*;
import java.util.*;


public class StudentSearchApp {
    VkAppSettings appSettings;
    VkApiClient vk;
    HttpRequestListener requestListener;

    private HttpServer server;
    private UserActor userActor;
    private GroupActor groupActor;
    private Map<Integer, String> groupAuthCodes;
    private List<Integer> groupIds;
    private List<List<String>> parsedText;
    //private Map<String, List<List<Integer>>> foundUsers;
    private Map<String, Integer> citiesId;

    public StudentSearchApp() {
        appSettings = new VkAppSettings();
        TransportClient transportClient = HttpTransportClient.getInstance();
        vk = new VkApiClient(transportClient);
        requestListener = new HttpRequestListener();
        try {
            citiesId = loadCities();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //foundUsers = new HashMap<>();
    }

    private Map<String, Integer> loadCities() throws IOException {
        Map<String, Integer> cities = new HashMap<>();
        try (BufferedReader  reader = new BufferedReader(new FileReader("src/main/resources/cities.txt"))) {
            String city = reader.readLine();
            while (city != null) {
                var split = city.split(";");
                cities.put(split[0].toLowerCase(), Integer.valueOf(split[1]));
                city = reader.readLine();
            }
        }
        return cities;
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

    public GroupActor getGroupActor() {
        return groupActor;
    }

    public List<List<String>> getParsedText() {
        return parsedText;
    }

    public List<Integer> getGroupIds() {
        return groupIds;
    }

    /**
     * Стартует приложение на локальном хосту и порту
     */
    public void startApp() {
        var httpHandler = new HttpSimpleHandler();
        httpHandler.registerListener(requestListener);
        try {
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 80), 0);
            server.createContext("/", httpHandler);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize userActor with params from config.properties.
     * Set group ids where userActor is admin
     * @return ErrorMessage
     */
    public void userAuthorization() throws InterruptedException, HttpException, IOException, ApiException, URISyntaxException, ClientException {
        userActor = Authorization.createUserActor(this);
        groupIds = vk.groups().get(userActor).filter(GroupsGetFilter.ADMIN).execute().getItems();
    }

    /**
     * Возвращает список найденных ids
     * @param participants
     */
    public List<List<Integer>> search(List<Query> participants, String listName) throws ClientException, ApiException, InterruptedException {
        Searcher searcher = new Searcher(this);
        var resultOfSearch = searcher.search(participants);
        //foundUsers.put(listName, resultOfSearch);
        return resultOfSearch;
    }

    public List<Query> handleCsvData(File file, List<String> fields) throws ClientException, ApiException, IOException {
        var parser = new Parser(this);
        return parser.csvParse(file, fields, true);
    }

    /**Возвращает просто текст из pdf построчно
     * @param source
     * @return
     * @throws IOException
     */
    public List<List<String>> getTextFromPdfDoc(File source) throws IOException {
        parsedText = DocParser.parse(source);
        return parsedText;
    }

    /**
     * Возвращает распарсенный построчный текст из parsedTest в виде списка Query
     * @param fields
     * @param ranges
     * @return
     * @throws ClientException
     * @throws ApiException
     */
    public List<Query> handlePdfData(List<String> fields, List<List<Pair<Integer, Integer>>> ranges) throws ClientException, ApiException {
        var parser = new Parser(this);
        List<Query> queries = new ArrayList<>();
        for (int page = 0; page < ranges.size(); page++) {
            queries.addAll(parser.pdfParse(page, fields, ranges.get(page)));
        }
        return queries;
    }

    /**
     * Create authentication code for groups
     * @return
     */
    public void createGroupAuthCode() throws InterruptedException, HttpException, IOException, ApiException, URISyntaxException, ClientException {
        groupAuthCodes = Authorization.getAuthGroupCodes(this);
    }

    /**
     * Create groupActor for group with id == groupId
     * @param groupId group id
     * @return ErrorMessage
     * @throws Exception if authentication code for group did not set
     */
    public void groupAuthorization(Integer groupId) throws Exception {
        if (groupAuthCodes == null) {
            createGroupAuthCode();
        }
        groupActor = Authorization.createGroupActor(groupId, groupAuthCodes);
    }

    /**
     * Read users id from string stringIds
     * @return List of ids
     */
    private List<Integer> getIds(String stringIds) {
        var ids = new ArrayList<Integer>();
        var idsArray = stringIds.split("\n");
        for (var id : idsArray)
        {
            try {
                ids.add(Integer.valueOf(id));
            } catch (NumberFormatException ex) {
                throw new NumberFormatException("Id is incorrect!");
            }
        }
        return ids;
    }

    /**
     * Read users id from file fileIds
     * @return List of ids
     */
    private List<Integer> getIds(File fileIds) throws FileNotFoundException, NumberFormatException {
        var ids = new ArrayList<Integer>();
        try (Scanner sc = new Scanner(fileIds)) {
            while(sc.hasNextLine()) {
                ids.add(Integer.valueOf(sc.nextLine()));
                System.out.println(ids.get(ids.size() - 1));
            }
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("Id is incorrect!");
        } catch (FileNotFoundException ex) {
            System.out.println(fileIds.getAbsolutePath());
            throw new FileNotFoundException("File with id did not find");
        }
        return ids;
    }

    public void addToFriend(List<Integer> usersIds) throws ClientException, ApiException {
        int countSend = 0;
        for(var user : usersIds) {
            var result = vk.friends().add(userActor, user).execute();
            if (result.getValue() == 1) {
                countSend++;
            }
        }
    }

}



