import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ClientSettings {
    String app_id;
    String access_token;
    String client_secret;
    String group_id;
    String redirect_uri;

    public ClientSettings() {
        Properties prop = new Properties();
        try
        {
            prop.load(new FileInputStream("config.properties"));
            app_id = prop.getProperty("app_id");
            access_token = prop.getProperty("access_token");
            client_secret = prop.getProperty("client_secret");
            group_id = prop.getProperty("group_id");
            redirect_uri = prop.getProperty("redirect_uri");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при загрузке файла конфигурации");
        }
    }
}
