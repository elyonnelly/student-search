package parser;

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DocParser {

    private static void strategy1(String source) throws IOException {
        PdfReader reader = new PdfReader(source);
        StringBuffer sb = new StringBuffer();
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
        TextExtractionStrategy strategy;
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            strategy = parser.processContent(i, new SimpleTextExtractionStrategy());
            sb.append(strategy.getResultantText());
            System.out.println(strategy.getResultantText() + "$$$");
        }
        reader.close();
        //System.out.println(sb.toString());
    }

    private static void strategy0(String source) throws IOException {
        PdfReader reader = new PdfReader(source);
        // не забываем, что нумерация страниц в PDF начинается с единицы.
        for (int i = 1; i <= reader.getNumberOfPages(); ++i) {
            TextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
            String text = PdfTextExtractor.getTextFromPage(reader, i, strategy);
            System.out.println(text);
        }

        // убираем за собой
        reader.close();
    }

    private static void strategy3Location(String source) throws IOException, NoSuchFieldException {
        PdfReader reader = new PdfReader(source);
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
        PrintWriter out = new PrintWriter(new FileOutputStream(source));
        TextExtractionStrategy strategy;
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            strategy
                    = parser.processContent(i, new HorizontalTextExtractionStrategy());
            out.println(strategy.getResultantText());
        }
        out.flush();
        out.close();
    }


    private static void extractText(String src) throws IOException {
        //PrintWriter out = new PrintWriter(new FileOutputStream(dest));
        PdfReader reader = new PdfReader(src);
        RenderListener listener = new MyTextRenderer();
        PdfContentStreamProcessor processor = new PdfContentStreamProcessor(listener);
        PdfDictionary pageDic = reader.getPageN(1);
        PdfDictionary resourcesDic = pageDic.getAsDict(PdfName.RESOURCES);
        processor.processContent(ContentByteUtils.getContentBytesForPage(reader, 1), resourcesDic);
        //out.flush();
        //out.close();
    }

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

