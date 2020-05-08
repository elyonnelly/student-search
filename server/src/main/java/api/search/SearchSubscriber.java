package api.search;

public interface SearchSubscriber {
    void update(MessageType type, int max);
    boolean isCancelled();
}
