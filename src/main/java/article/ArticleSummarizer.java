package article;

import data.Article;
import data.Section;
import data.Sections;
import data.Simple;
import net.htmlparser.jericho.Source;
import net.sf.classifier4J.summariser.SimpleSummariser;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArticleSummarizer {

    private static SimpleSummariser summarizer;

    private final Article article;

    public ArticleSummarizer(Article article) {
        this.summarizer = new SimpleSummariser();
        this.article = article;
    }

    public String getAllContent() {
        StringBuilder sb = new StringBuilder();
        for(Section section : article.getSections()) {
            sb.append(section.getContent());
        }
        return sb.toString();
    }

    public String stripHtml(String html) {
        Source src = new Source(html);
        return src.getTextExtractor().toString();
    }

    // http://stackoverflow.com/a/2687929
    public List<String> splitInLines2(String content) {
        List<String> lines = new ArrayList<>();
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
        iterator.setText(content);
        int start = iterator.first();
        for (int end = iterator.next();
             end != BreakIterator.DONE;
             start = end, end = iterator.next()) {
            String line = content.substring(start,end);
            lines.add(line);
        }
        return lines;
    }

    // http://stackoverflow.com/a/21430792
    public List<String> splitInLines(String content) {
        List<String> lines = new ArrayList<>();
        String pattern = "[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)";
        Pattern re = Pattern.compile(pattern, Pattern.MULTILINE | Pattern.COMMENTS);
        Matcher reMatcher = re.matcher(content);
        while (reMatcher.find()) {
            String group = reMatcher.group();
            lines.add(group);
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

    public String summarize(String text, int numSentences) {
        return summarizer.summarise(text, numSentences);
    }

    public Article getSummarizedArticle(float reductionFactor) {
        if (reductionFactor < 0 || reductionFactor > 1) {
            throw new RuntimeException();
        }
        List<Section> summarizedSectionList = new ArrayList<>();
        // Dividimos el contenido del artículo en líneas individuales para saber cuántas hay,
        // y las juntamos en un String para resumirlo en un % de sus líneas originales
        List<String> originalArticleLines = splitInLines(stripHtml(getAllContent()));
        String originalArticleContent = joinLines(originalArticleLines);
        String summarizedArticleContent = summarize(originalArticleContent,
                Math.round(originalArticleLines.size() * reductionFactor));
        // El contenido resumido lo dividimos en líneas para poder compararlas
        // con las del artículo original
        List<String> summarizedArticleLines = splitInLines(summarizedArticleContent);

        System.out.println("\nOriginal article lines:");
        printList(originalArticleLines);
        System.out.println("\nSummarized article lines:");
        printList(summarizedArticleLines);

        for(Section section : article.getSections()) {
            Section summarizedSection = new Section(section.getRawSection(), "");

            summarizedSectionList.add(summarizedSection);
        }
        Sections summarizedSections = new Sections(summarizedSectionList);
        Article summarizedArticle = new Article(this.article.getTitle(), summarizedSections);
        return summarizedArticle;
    }


    public void printList(List<String> list) {
        int i = 1;
        for(String elem : list) {
            System.out.println("(" + i++ + "/" + list.size() + ")\t" + elem);
        }
    }

    public Article getArticle() {
        return article;
    }

}
