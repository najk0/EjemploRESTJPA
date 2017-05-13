package article;

import data.Article;
import data.Section;
import net.htmlparser.jericho.Source;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArticleSummarizer {

    private final Article article;

    public ArticleSummarizer(Article article) {
        this.article = article;
    }

    // http://stackoverflow.com/a/21430792
    public List<String> splitInLines(String html) {
        List<String> lines = new ArrayList<>();
        String pattern = "[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)";
        Pattern re = Pattern.compile(pattern, Pattern.MULTILINE | Pattern.COMMENTS);
        Matcher reMatcher = re.matcher(html);
        while (reMatcher.find()) {
            lines.add(reMatcher.group());
        }
        return lines;
    }

    public String joinLines(List<String> lines) {
        StringBuilder sb = new StringBuilder();
        for(String line : lines) {
            sb.append(line);
        }
        return sb.toString();
    }

    public List<String> getAllLines() {
        List<String> allLines = new ArrayList<>();
        for(Section section : article.getSections()) {
            List<String> sectionLines = splitInLines(section.getContent());
            allLines.addAll(sectionLines);
        }
        return allLines;
    }

    public List<String> getAllLinesPlaintext() {
        List<String> allLines = new ArrayList<>();
        for(Section section : article.getSections()) {
            Source src = new Source(section.getContent());
            List<String> sectionLines = splitInLines(src.getTextExtractor().toString());
            allLines.addAll(sectionLines);
        }
        return allLines;
    }

    public Article getSummarizedArticle() {
        return null;
    }

    public Article getArticle() {
        return article;
    }

}
