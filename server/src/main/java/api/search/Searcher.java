package api.search;

import api.StudentSearchApp;
import api.query.Query;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.database.School;
import com.vk.api.sdk.objects.users.UserFull;

import java.util.ArrayList;
import java.util.List;

public class Searcher {
    StudentSearchApp app;

    public Searcher(StudentSearchApp app) {
        this.app = app;
    }


    private List<School> findSchoolVKDB(int cityId, String value) throws ClientException, ApiException {
        var result = app.getVK().database().getSchools(app.getUserActor(), cityId).q(value).execute().getItems();
        try {
            Thread.sleep(340);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void findSchool(Query query) throws ClientException, ApiException {
        //пытаемся найти школу в городе, ы.
        if (query.getCityId() == null) {
            return;
        }
        String value = query.getSchool();
        var cityId = query.getCityId();
        //пробуем найти школу целиком
        var schools = findSchoolVKDB(cityId, value);
        if (schools.size() != 0) {
            query.setSchoolId(schools.get(0).getId());
        }
        else {
            //ищем только номер ее
            var splitValue = value.split("№");
            if (splitValue.length > 1) {
                var schoolValue = splitValue[1].split(" ");
                schools = findSchoolVKDB(cityId, schoolValue[0]);
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

        try {
            Thread.sleep(340);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (res.size() > 0) {
            if (res.size() != 1) {
                findSchool(q);
                if (q.getSchoolId() != null) {
                    vkQuery.school(q.getSchoolId());
                    res = vkQuery.execute().getItems();try {
                        Thread.sleep(340);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return res;
        }
        else {
            return new ArrayList<>();
        }
    }

    public List<List<Integer>> search(List<Query> usersQuery) throws ClientException, ApiException, InterruptedException {
        List<List<Integer>> result = new ArrayList<>();
        result.add(new ArrayList<>()); //winnert
        result.add(new ArrayList<>());//prizewinner
        result.add(new ArrayList<>());//participant
        int count = 0;
        for(var user : usersQuery) {
            var res = executeQuery(user);
            for(var us : res) {
                System.out.println(user.getQ());
                count++;
                result.get(user.getStatusParticipant().getValue()).add(us.getId());
                System.out.println(us.getId());
            }
            if (count > 50) {
                Thread.sleep(3000);
                count = 0;
            }
        }
        return result;
    }

}
