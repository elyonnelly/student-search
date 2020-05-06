import api.StudentSearchApp;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import parser.DocParser;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ParserTest {

    @Test
    void parse() throws IOException, NoSuchFieldException {
        DocParser.parse(new File("test2.pdf"));
    }


}
