package parser;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DocParser {

    private static List<List<String>> strategy(File source) throws IOException {
        PdfReader reader = new PdfReader(new FileInputStream(source));
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
        MyTextRenderer strategy;
        List<List<String>> textByPage = new ArrayList<>();
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            strategy = parser.processContent(i, new MyTextRenderer());
            textByPage.add(strategy.getWords());
        }
        reader.close();
        return textByPage;
    }

    public static List<List<String>> parse(File source) throws IOException {
        return strategy(source);
    }

}

