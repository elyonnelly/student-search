package api.search;

import api.StudentSearchApp;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;

public class CountGroupMemberCommand extends RequestCommand implements Command<Integer, Integer> {

    String groupId;
    Integer vkResponse;
    int count;

    protected CountGroupMemberCommand(StudentSearchApp app, String groupId) {
        super(app);
        this.groupId = groupId;
    }

    @Override
    public void handleRequest(Integer id) throws ClientException, ApiException {
        vkResponse = app.getVK().groups().isMember(app.getUserActor(), groupId).userId(id).execute().getValue();
    }

    @Override
    public void businessLogic(Integer item) {
        count += vkResponse;
    }

    @Override
    public Integer getValue() {
        return count;
    }
}
