package article;

import data.Article;
import data.Content;
import data.Section;
import data.Sections;
import html.HtmlSanitizer;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArticleParser {

    private final Article article;


    public ArticleParser(Article article) {
        this.article = article;
    }

    public String keepOnlyParagraphTags(String html) {
        StringBuilder sb = new StringBuilder();
        Source src = new Source(html);
        List<Element> elements = src.getAllElements(HTMLElementName.P);
        for(Element e : elements) {
            if (e.isEmpty() == false) {
                sb.append(e);
            }
        }
        return sb.toString();
    }

    private String replaceOccurencesWith(String html, String target, String replacement) {
        Pattern pattern = Pattern.compile(target);
        Matcher matcher = pattern.matcher(html);
        String modifiedHtml = html;
        while(matcher.find()) {
            modifiedHtml = modifiedHtml.replace(matcher.group(), replacement); // Wow!
        }
        return modifiedHtml;
    }

    public String stripAllButLinks(String html) {
        String noLinksHtml = new HtmlSanitizer().sanitize(html);
        String noReferencesHtml = replaceOccurencesWith(noLinksHtml, "\\[(\\d*?)\\]", "");
        String properlySpacedHtml = replaceOccurencesWith(noReferencesHtml, "(<\\/p>)", " </p>"); // TODO remove
        return properlySpacedHtml;
    }

    public String stripTagsOfSection(int index) {
        return stripTagsOfSection(article.getSections().getByIndex(index));
    }

    public String stripTagsOfSection(Section section) {
        String onlyPTags = keepOnlyParagraphTags(section.getDisplayedContent());
        return stripAllButLinks(onlyPTags);
    }

    public Article getParsedArticle() {
        List<Section> parsedSectionList = new ArrayList<>();
        for(Section section : article.getSections()) {
            Section parsedSection = new Section(section.getRawSection());
            String strippedContent = stripTagsOfSection(section);
            parsedSection.setContent(new Content(strippedContent));
            parsedSection.setDisplayedContent(strippedContent);
            parsedSectionList.add(parsedSection);
        }
        Sections parsedSections = new Sections(parsedSectionList);
        Article parsedArticle = new Article(this.article.getTitle(), parsedSections);
        return parsedArticle;
    }

}
