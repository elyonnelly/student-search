package api.search;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;

import java.util.Random;

public interface Command<T, R> {
    void handleRequest(T item) throws ClientException, ApiException;
    void businessLogic(T item);
    R getValue();

    default void delay(int max) {
        Random rnd = new Random();
        try {
            if (max > 50) {
                Thread.sleep(340 + rnd.nextInt(3000));
            }
            else {
                Thread.sleep(340);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
