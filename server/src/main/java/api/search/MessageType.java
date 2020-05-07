package api.search;

public enum MessageType {
    ONE_MORE(1),
    CANCEL(3),
    READY(2);

    private final int value;

    MessageType(int value) {
        this.value = value;
    }
}
