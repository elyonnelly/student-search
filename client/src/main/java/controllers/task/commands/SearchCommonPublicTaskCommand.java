package controllers.task.commands;

import api.StudentSearchApp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchCommonPublicTaskCommand extends TaskCommandBase {
    private List<Integer> userIds;

    public SearchCommonPublicTaskCommand(StudentSearchApp app, List<Integer> userIds) {
        super(app);
        this.userIds = userIds;
    }

    @Override
    public ArrayList<Map.Entry<Integer, Integer>> execute() throws Exception {
        return app.getSearcher().searchCommonPublic(userIds);
    }
}
