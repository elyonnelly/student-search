package controllers.task.commands;

import api.StudentSearchApp;
import api.parse.Query;

import java.util.List;

public class FindUsersTaskCommand extends TaskCommandBase{

    private List<Query> queries;
    private String listTitle;
    private boolean append;

    public FindUsersTaskCommand(StudentSearchApp app, List<Query> queries, String listTitle, boolean append) {
        super(app);
        this.queries = queries;
        this.listTitle = listTitle;
        this.append = append;
    }

    @Override
    public List<List<Integer>> execute() throws Exception {
        return app.search(queries, listTitle, append);
    }
}
