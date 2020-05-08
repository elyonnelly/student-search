package controllers.task.commands;

import api.StudentSearchApp;

import java.util.List;

public class CountGroupMemberTaskCommand extends TaskCommandBase {

    private List<Integer> userIds;
    private String groupId;

    public CountGroupMemberTaskCommand(StudentSearchApp app, List<Integer> userIds, String groupId) {
        super(app);
        this.userIds = userIds;
        this.groupId = groupId;
    }

    @Override
    public Integer execute() throws Exception {
        return app.getSearcher().getGroupMembers(userIds, groupId);
    }
}
