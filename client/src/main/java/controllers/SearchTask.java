package controllers;

import api.StudentSearchApp;
import api.parse.Query;
import api.search.MessageType;
import api.search.SearchSubscriber;
import javafx.concurrent.Task;

import java.util.List;

/**
 * класс controllers.SearchTask прослушивает происходящее в объекте Searcher, который отправляет controllers.SearchTask сообщения о ходе поисков
 */
class SearchTask extends Task<List<List<Integer>>> implements SearchSubscriber {

    private StudentSearchApp app;
    private List<Query> queries;
    private String listTitle;
    private int process;
    private boolean append;

    SearchTask(StudentSearchApp app, List<Query> queries, String listTitle, boolean append) {
        this.app = app;
        this.queries = queries;
        this.listTitle = listTitle;
        this.append = append;
    }

    @Override
    protected List<List<Integer>> call() throws Exception {
        app.getSearcher().subscribe(this);
        var result = app.fictitiousSearch(queries, listTitle, append);
        app.getSearcher().unsubscribe(this);
        return result;
    }

    @Override
    public void update(MessageType messageType) {
        System.out.println(messageType);
        switch (messageType) {
            case ONE_MORE: {
                process++;
                updateProgress(process, 10);
                break;
            }
            case READY: {
                updateProgress(10, 10);
                break;
            }
            case CANCEL: {
                updateProgress(0, 10);
                cancel();
                break;
            }
        }
    }
    @Override protected void succeeded() {
        super.succeeded();
        updateMessage("Done!");
    }
}
