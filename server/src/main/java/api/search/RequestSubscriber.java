package api.search;

public interface RequestSubscriber {
    void update(MessageType type, int max);
    boolean isCancelled();
}
