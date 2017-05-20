import api.WikiAPI;
import article.ArticleParser;
import article.ArticleSplitter;
import article.ArticleSummarizer;
import data.Article;
import net.sf.classifier4J.summariser.SimpleSummariser;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ArticleSummarizerTest {

    private static WikiAPI api;

    private static ArticleSummarizer summarizer;

    private static SimpleSummariser classifier4JSummariser;

    @BeforeClass
    public static void init() {
        api = new WikiAPI();
        classifier4JSummariser = new SimpleSummariser();
        JSONObject json = api.getArticleJSON("Queen (band)");

        ArticleSplitter splitter = new ArticleSplitter(json);
        Article splitArticle = splitter.getSplitArticle();

        ArticleParser parser = new ArticleParser(splitArticle);
        Article parsedArticle = parser.getParsedArticle();

        summarizer = new ArticleSummarizer(parsedArticle);
    }

}
