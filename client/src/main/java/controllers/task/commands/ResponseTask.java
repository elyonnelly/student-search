package controllers.task.commands;

import api.StudentSearchApp;
import api.search.MessageType;
import api.search.SearchSubscriber;
import controllers.task.commands.TaskCommand;
import javafx.concurrent.Task;

public class ResponseTask<T> extends Task<T> implements SearchSubscriber {

    private StudentSearchApp app;
    private double process;
    TaskCommand<T> command;

    public ResponseTask(StudentSearchApp app, TaskCommand command) {
        this.app = app;
        this.command = command;
    }

    @Override
    public void update(MessageType messageType, int max) {
        System.out.println(messageType);
        switch (messageType) {
            case ONE_MORE: {
                process++;
                updateProgress(process, max);
                break;
            }
            case READY: {
                updateProgress(max, max);
                break;
            }
            case CANCEL: {
                updateProgress(0, max);
                cancel();
                break;
            }
        }
    }

    @Override
    protected T call() throws Exception {
        app.getSearcher().subscribe(this);
        var result = command.execute();
        app.getSearcher().unsubscribe(this);
        return result;
    }
}
