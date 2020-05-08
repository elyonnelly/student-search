package controllers.task.commands;

public interface TaskCommand<T> {
    T execute() throws Exception;
}
