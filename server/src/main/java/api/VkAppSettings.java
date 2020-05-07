package api;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

class VkAppSettings {
    Integer app_id;
    String client_secret;
    String redirect_uri;

    VkAppSettings() throws IOException {
        Properties prop = new Properties();
        try
        {
            prop.load(new FileInputStream("config.properties"));
            app_id = Integer.valueOf(prop.getProperty("app_id"));
            client_secret = prop.getProperty("client_secret");
            redirect_uri = prop.getProperty("redirect_uri");
        } catch (IOException e){
            throw  new IOException("Ошибка при загрузке файла конфигурации");
        }
    }
}
