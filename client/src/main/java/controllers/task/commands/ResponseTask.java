package controllers.task.commands;

import api.StudentSearchApp;
import api.search.MessageType;
import api.search.RequestSubscriber;
import javafx.concurrent.Task;

public class ResponseTask<T> extends Task<T> implements RequestSubscriber {

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
        app.getRequester().subscribe(this);
        var result = command.execute();
        app.getRequester().unsubscribe(this);
        return result;
    }
}
