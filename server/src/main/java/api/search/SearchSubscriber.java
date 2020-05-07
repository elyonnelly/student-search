package api.search;

public interface SearchSubscriber {
    void update(MessageType type);
    boolean isCancelled();
}
