package api.search;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;

public interface Command<T, R> {
    void handleRequest(T item) throws ClientException, ApiException;
    void businessLogic(T item);
    R getValue();

    default void delay() {
        try {
            Thread.sleep(340);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
