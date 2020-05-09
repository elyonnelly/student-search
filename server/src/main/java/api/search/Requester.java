package api.search;

import api.StudentSearchApp;
import api.parse.Query;
import com.vk.api.sdk.exceptions.ApiAccessException;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Requester {
    private StudentSearchApp app;
    private List<RequestSubscriber> searchSubscribers;

    public Requester(StudentSearchApp app) {
        this.app = app;
        searchSubscribers = new ArrayList<>();
    }

    public void subscribe(RequestSubscriber s) {
        searchSubscribers.add(s);
    }

    public void unsubscribe(RequestSubscriber s) {
        searchSubscribers.remove(s);
    }

    private void notifySearchSubscribers(MessageType messageType, int max) {
        for (int i = 0; i < searchSubscribers.size(); i++) {
            searchSubscribers.get(i).update(messageType, max);
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
     * Возвращает количество пользователей в группе groupId среди пользователей userIds
     * @param userIds id пользователей
     * @param groupId id группы
     */
    public int countGroupMembers(List<Integer> userIds, String groupId) {
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
            } catch (ApiAccessException ex){
                continue;
            }
            catch (ClientException | ApiException e) {
                e.printStackTrace();
                notifySearchSubscribers(MessageType.CANCEL, itemsRequest.size());
                return handler.getValue();
            }
            handler.delay(itemsRequest.size());
            handler.businessLogic(item);
            notifySearchSubscribers(MessageType.ONE_MORE, itemsRequest.size());
        }
        notifySearchSubscribers(MessageType.READY, itemsRequest.size());
        return handler.getValue();
    }

}
