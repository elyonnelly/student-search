package api.search;

import api.StudentSearchApp;
import api.parse.Query;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Searcher {
    private StudentSearchApp app;
    private List<SearchSubscriber> searchSubscribers;

    public Searcher(StudentSearchApp app) {
        this.app = app;
        searchSubscribers = new ArrayList<>();
    }

    public void subscribe(SearchSubscriber s) {
        searchSubscribers.add(s);
    }

    public void unsubscribe(SearchSubscriber s) {
        searchSubscribers.remove(s);
    }

    private void notifySearchSubscribers(MessageType messageType) {
        for (int i = 0; i < searchSubscribers.size(); i++) {
            searchSubscribers.get(i).update(messageType);
        }
    }

    /**
     * @return остановлен ли поиск
     */
    private boolean isCancelled() {
        for (var subscribers : searchSubscribers) {
            if (subscribers.isCancelled()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Фиктивный поиск, для отладки. "Возвращает список найденных id"
     * @param participants список пользователей для поиска
     * @return resultOfSearch - список найденных id, где resultOfSearch[0] - список победителей, [2] - призеров, [3] - участников
     * Если статус участника не указан, все добавляются в список участников
     */
    public List<List<Integer>> fictitiousSearch(List<Query> participants) {
        //var result = loadListFile(listName);
        List<List<Integer>> result = new ArrayList<>();
        result.add(new ArrayList<>()); //winnert
        result.add(new ArrayList<>());//prizewinner
        result.add(new ArrayList<>());//participant
        result.get(2).add(493076358);
        result.get(2).add(528087524);
        result.get(2).add(184094713);
        result.get(2).add(197211347);
        result.get(2).add(182402497);
        for (int i = 0; i < 1; i++) {
            if (isCancelled()) {
                System.out.println("isCancelled");
                return result;
            }
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                notifySearchSubscribers(MessageType.CANCEL);
            }
            notifySearchSubscribers(MessageType.ONE_MORE);
        }
        notifySearchSubscribers(MessageType.READY);
        return result;
    }

    /**
     * Возвращает количество пользователей в группе groupId среди пользователей userIds
     * @param userIds id пользователей
     * @param groupId id группы
     */
    public int getGroupMembers(List<Integer> userIds, String groupId) {
        Command groupMembers = new CountGroupMemberCommand(app, groupId);
        return (int) handleRequest(userIds, groupMembers);
    }

    /**
     * Возвращает отсортированный по количеству участников из выборки список общих для пользователей групп и публичных страниц.
     * @param userIds список пользователей
     * @return список общих пабликов. Key - id группы, Value - количество пользователей в ней
     */
    public ArrayList<Map.Entry<Integer, Integer>> searchCommonPublic(List<Integer> userIds){
        Command commonPublicCommand = new SearchCommonPublicCommand(app);
        return (ArrayList<Map.Entry<Integer, Integer>>) handleRequest(userIds, commonPublicCommand);
    }

    /**
     * Возвращает список найденных id
     * @param usersQuery список пользователей для поиска
     * @return resultOfSearch - список найденных id, где resultOfSearch[0] - список победителей, [2] - призеров, [3] - участников
     * Если статус участника не указан, все добавляются в список участников
     */
    public List<List<Integer>> search(List<Query> usersQuery) {
        Command finUserCommand = new FindUsersCommand(app);
        return (List<List<Integer>>) handleRequest(usersQuery, finUserCommand);
    }

    private Object handleRequest(List itemsRequest, Command handler) {
        for (var item : itemsRequest) {
            if (isCancelled()) {
                return handler.getValue();
            }
            try {
                handler.handleRequest(item);
            } catch (ClientException | ApiException e) {
                notifySearchSubscribers(MessageType.CANCEL);
                return handler.getValue();
            }
            handler.delay();
            handler.businessLogic(item);
            notifySearchSubscribers(MessageType.ONE_MORE);
        }
        notifySearchSubscribers(MessageType.READY);
        return handler.getValue();
    }

}
