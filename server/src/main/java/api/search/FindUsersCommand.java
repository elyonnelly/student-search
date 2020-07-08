package api.search;

import api.StudentSearchApp;
import api.parse.Query;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.database.School;
import com.vk.api.sdk.objects.users.UserFull;

import java.util.ArrayList;
import java.util.List;

public class FindUsersCommand extends RequestCommand implements Command<Query, List<List<Integer>>> {
    private List<List<Integer>> ids;
    private List<UserFull> userId;

    public FindUsersCommand(StudentSearchApp app) {
        super(app);
        ids = new ArrayList<>();
        ids.add(new ArrayList<>()); //winnert
        ids.add(new ArrayList<>());//prizewinner
        ids.add(new ArrayList<>());//participant
        this.app = app;
    }


    @Override
    public void handleRequest(Query user) throws ClientException, ApiException {
        userId = executeQuery(user);
    }

    @Override
    public void businessLogic(Query user) {
        for(var us : userId) {
            ids.get(user.getStatusParticipant().getValue()).add(us.getId());
        }
    }

    @Override
    public List<List<Integer>> getValue() {
        return ids;
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
        delay(0);

        if (res.size() > 0) {
            if (res.size() != 1) {
                setSchoolId(q);
                if (q.getSchoolId() != null) {
                    vkQuery.school(q.getSchoolId());
                    var newRes = vkQuery.execute().getItems();
                    delay(0);
                    if (newRes.size() > 0) {
                        return newRes;
                    }
                }
            }
            return res;
        }
        else {
            return new ArrayList<>();
        }
    }


    private List<School> executeGetSchools(int cityId, String value) throws ClientException, ApiException {
        var result = app.getVK().database().getSchools(app.getUserActor(), cityId).q(value).execute().getItems();
        delay(0);
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
}
