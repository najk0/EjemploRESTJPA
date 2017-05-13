package article;

import data.Article;
import data.Section;
import data.Sections;
import html.HtmlSanitizer;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import java.util.ArrayList;
import java.util.List;

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

    public String stripAllButLinks(String html) {
        return new HtmlSanitizer().sanitize(html);
    }

    //TODO quitar tags a cuyo ("href").contains("#cite_note")
    public String stripTagsOfSection(int index) {
        return stripTagsOfSection(article.getSections().getByIndex(index));
    }

    public String stripTagsOfSection(Section section) {
        String onlyPTags = keepOnlyParagraphTags(section.getContent());
        return stripAllButLinks(onlyPTags);
    }

    public Article getParsedArticle() {
        List<Section> parsedSectionList = new ArrayList<>();
        for(Section section : article.getSections()) {
            Section parsedSection = new Section(section.getRawSection(), stripTagsOfSection(section));
            parsedSectionList.add(parsedSection);
        }
        Sections parsedSections = new Sections(parsedSectionList);
        Article parsedArticle = new Article(this.article.getTitle(), parsedSections);
        return parsedArticle;
    }

}
