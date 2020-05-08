package api.search;

import api.StudentSearchApp;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.groups.GroupsArray;
import org.bouncycastle.ocsp.Req;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class SearchCommonPublicCommand extends RequestCommand implements Command<Integer, ArrayList<Map.Entry<Integer, Integer>>> {

    Map<Integer, Integer> commonPublic;
    GroupsArray groupsArray;

    protected SearchCommonPublicCommand(StudentSearchApp app) {
        super(app);
        commonPublic = new HashMap<>();
    }

    @Override
    public void handleRequest(Integer id) throws ClientException, ApiException {
        groupsArray = app.getVK().users().getSubscriptions(app.getUserActor()).userId(id).execute().getGroups();
    }

    @Override
    public void businessLogic(Integer item) {
        for (var groupId : groupsArray.getItems()) {
            commonPublic.put(groupId, commonPublic.getOrDefault(groupId, 0) + 1);
        }
    }

    @Override
    public ArrayList<Map.Entry<Integer, Integer>> getValue() {
        ArrayList<Map.Entry<Integer, Integer>> entryList = new ArrayList<>(commonPublic.entrySet());
        entryList.sort(new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        return entryList;
    }
}
