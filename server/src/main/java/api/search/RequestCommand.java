package api.search;

import api.StudentSearchApp;

public class RequestCommand {
    protected StudentSearchApp app;

    protected RequestCommand(StudentSearchApp app) {
        this.app = app;
    }
}
