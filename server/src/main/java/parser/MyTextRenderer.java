package parser;

import com.itextpdf.text.pdf.parser.*;

import java.util.ArrayList;
import java.util.List;

public class MyTextRenderer implements TextExtractionStrategy {
    private Vector lastStart;
    private Vector lastEnd;
    private final StringBuffer result = new StringBuffer();
    private StringBuilder newWord;
    private List<String> words;

    MyTextRenderer() {
        newWord = new StringBuilder();
        words = new ArrayList<>();
    }

    public void beginTextBlock() {
    }

    public void endTextBlock() {
    }

    /**
     *
     * @return Return all tokens that PDFReader found in doc
     */
    List<String> getWords() {
        return words;
    }

    public String getResultantText() {
        return this.result.toString();
    }

    private void appendTextChunk(CharSequence text) {
        this.result.append(text);
    }

    public void renderText(TextRenderInfo renderInfo) {
        boolean firstRender = this.result.length() == 0;
        boolean hardReturn = false;
        LineSegment segment = renderInfo.getBaseline();
        Vector start = segment.getStartPoint();
        Vector end = segment.getEndPoint();
        if (!firstRender) {
            Vector x1 = this.lastStart;
            Vector x2 = this.lastEnd;
            float dist = x2.subtract(x1).cross(x1.subtract(start)).lengthSquared() / x2.subtract(x1).lengthSquared();
            float sameLineThreshold = 1.0F;
            if (dist > sameLineThreshold) {
                hardReturn = true;
            }
        }

        if (hardReturn) {
            words.add(newWord.toString());
            newWord = new StringBuilder();
            this.appendTextChunk(";");
        } else if (!firstRender && this.result.charAt(this.result.length() - 1) != ' ' && renderInfo.getText().length() > 0 && renderInfo.getText().charAt(0) != ' ') {
            float spacing = this.lastEnd.subtract(start).length();
            if (spacing > renderInfo.getSingleSpaceWidth() / 2.0F) {
                words.add(newWord.toString());
                newWord = new StringBuilder();
                this.appendTextChunk(";");
            }
        }

        this.appendTextChunk(renderInfo.getText());
        newWord.append(renderInfo.getText());
        this.lastStart = start;
        this.lastEnd = end;
    }

    public void renderImage(ImageRenderInfo renderInfo) {
    }
}

