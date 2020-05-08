package controllers.task.commands;

import api.StudentSearchApp;

public abstract class TaskCommandBase implements TaskCommand {
    protected StudentSearchApp app;

    protected TaskCommandBase(StudentSearchApp app) {
        this.app = app;
    }
}
