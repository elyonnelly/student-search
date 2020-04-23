import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class VkAppSettings {
    String app_id;
    String access_token;
    String client_secret;
    Integer user_id;
    Integer group_id;
    String redirect_uri;

    public VkAppSettings() {
        Properties prop = new Properties();
        try
        {
            prop.load(new FileInputStream("config.properties"));
            app_id = prop.getProperty("app_id");
            access_token = prop.getProperty("access_token");
            client_secret = prop.getProperty("client_secret");
            group_id = Integer.valueOf(prop.getProperty("group_id"));
            redirect_uri = prop.getProperty("redirect_uri");
            user_id = Integer.valueOf(prop.getProperty("user_id"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при загрузке файла конфигурации");
        }
    }
}
