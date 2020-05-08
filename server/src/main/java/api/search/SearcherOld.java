package api.search;

import api.StudentSearchApp;
import api.parse.Query;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.database.School;
import com.vk.api.sdk.objects.groups.GroupsArray;
import com.vk.api.sdk.objects.users.UserFull;

import java.util.*;

class SearcherOld {
    private StudentSearchApp app;
    private List<SearchSubscriber> searchSubscribers;

    public SearcherOld(StudentSearchApp app) {
        this.app = app;
        searchSubscribers = new ArrayList<>();
    }

    public void subscribe(SearchSubscriber s) {
        searchSubscribers.add(s);
    }

    public void unsubscribe(SearchSubscriber s) {
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
                notifySearchSubscribers(MessageType.CANCEL, 10);
            }
            notifySearchSubscribers(MessageType.ONE_MORE, 10);
        }
        notifySearchSubscribers(MessageType.READY, 10);
        return result;
    }

    /**
     * Возвращает количество пользователей в группе groupId среди пользователей userIds
     * @param userIds id пользователей
     * @param groupId id группы
     */
    public int getGroupMembers(List<Integer> userIds, String groupId) {
        int count = 0;
        for (var id : userIds) {
            Integer vkQuery = null;
            try {
                vkQuery = app.getVK().groups().isMember(app.getUserActor(), groupId).userId(id).execute().getValue();
            } catch (ApiException e) {
                e.printStackTrace();
            } catch (ClientException e) {
                e.printStackTrace();
            }
            //задержка для обхода блокировки на кол-во запросов
            delay();
            count += vkQuery;
        }
        return count;
    }

    /**
     * Возвращает отсортированный по количеству участников из выборки список общих для пользователей групп и публичных страниц.
     * @param userIds список пользователей
     * @return список общих пабликов. Key - id группы, Value - количество пользователей в ней
     */
    public ArrayList<Map.Entry<Integer, Integer>> searchCommonPublic(List<Integer> userIds){
        Map<Integer, Integer> commonPublic = new HashMap<>();
        for (var id :userIds) {
            GroupsArray vkQuery = null;
            try {
                vkQuery = app.getVK().users().getSubscriptions(app.getUserActor()).userId(id).execute().getGroups();
            } catch (ApiException e) {
                e.printStackTrace();
            } catch (ClientException e) {
                e.printStackTrace();
            }
            delay();
            for (var groupId : vkQuery.getItems()) {
                commonPublic.put(groupId, commonPublic.getOrDefault(groupId, 0) + 1);
            }
        }
        ArrayList<Map.Entry<Integer, Integer>> entryList = new ArrayList<>(commonPublic.entrySet());
        entryList.sort(new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        return entryList;
    }

    /**
     * Возвращает список найденных id
     * @param usersQuery список пользователей для поиска
     * @return resultOfSearch - список найденных id, где resultOfSearch[0] - список победителей, [2] - призеров, [3] - участников
     * Если статус участника не указан, все добавляются в список участников
     */
    public List<List<Integer>> search(List<Query> usersQuery) {
        List<List<Integer>> ids = new ArrayList<>();
        ids.add(new ArrayList<>()); //winnert
        ids.add(new ArrayList<>());//prizewinner
        ids.add(new ArrayList<>());//participant
        int count = 0;
        for(var user : usersQuery) {
            if (isCancelled()) {
                return ids;
            }
            List<UserFull> userId;
            try {
                userId = executeQuery(user);
            } catch (ClientException| ApiException e) {
                notifySearchSubscribers(MessageType.CANCEL, usersQuery.size());
                return ids;
            }
            for(var us : userId) {
                System.out.println(user.getQ());
                count++;
                ids.get(user.getStatusParticipant().getValue()).add(us.getId());
                System.out.println(us.getId());
            }
            notifySearchSubscribers(MessageType.ONE_MORE, usersQuery.size());
        }
        notifySearchSubscribers(MessageType.READY, usersQuery.size());
        return ids;
    }

    private List<School> executeGetSchools(int cityId, String value) throws ClientException, ApiException {
        var result = app.getVK().database().getSchools(app.getUserActor(), cityId).q(value).execute().getItems();
        delay();
        return result;
    }

    private void setSchoolId(Query query) throws ClientException, ApiException {
        //пытаемся найти школу в городе
        if (query.getCityId() == null) {
            return;
        }
        String value = query.getSchool();
        var cityId = query.getCityId();
        //пробуем найти школу целиком
        var schools = executeGetSchools(cityId, value);
        if (schools.size() != 0) {
            query.setSchoolId(schools.get(0).getId());
        }
        else {
            //ищем только номер ее
            var splitValue = value.split("№");
            if (splitValue.length > 1) {
                var schoolValue = splitValue[1].split(" ");
                schools = executeGetSchools(cityId, schoolValue[0]);
                if (schools.size() != 0) {
                    query.setSchoolId(schools.get(0).getId());
                    query.setSchool(value);
                }
            }
        }
    }

    private List<UserFull> executeQuery(Query q) throws ClientException, ApiException {
        var vkQuery = app.getVK().users().search(app.getUserActor());
        vkQuery.ageFrom(q.getAge_from());
        vkQuery.ageTo(q.getAge_to());
        if (q.getQ() != null) {
            vkQuery.q(q.getQ());
        }
        if (q.getCityId() != null) {
            vkQuery.city(q.getCityId());
        }

        var res = vkQuery.execute().getItems();
        delay();

        if (res.size() > 0) {
            if (res.size() != 1) {
                setSchoolId(q);
                if (q.getSchoolId() != null) {
                    vkQuery.school(q.getSchoolId());
                    res = vkQuery.execute().getItems();
                    delay();
                }
            }
            return res;
        }
        else {
            return new ArrayList<>();
        }
    }

    private void delay() {
        try {
            Thread.sleep(340);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //TODO вставить какие-то левые случайные вызовы
    }

}
