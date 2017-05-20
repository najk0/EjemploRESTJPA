import api.WikiAPI;
import article.ArticleParser;
import article.ArticleSplitter;
import article.ArticleSummarizer;
import data.Article;
import net.sf.classifier4J.Utilities;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

public class TemporaryTests {

    private static WikiAPI api;

    @BeforeClass
    public static void init() {
        api = new WikiAPI();
    }

    @Test
    public void splitterTest() throws Exception {
        String title = "Wicket gate";
        JSONObject json = api.getArticleJSON(title);
        ArticleSplitter splitter = new ArticleSplitter(json);
        Article splitArticle = splitter.getSplitArticle();
        System.out.println(splitArticle);
    }

    @Test
    public void parserTest() throws Exception {
        String title = "Society";
        JSONObject json = api.getArticleJSON(title);
        ArticleSplitter splitter = new ArticleSplitter(json);
        Article splitArticle = splitter.getSplitArticle();
        ArticleParser parser = new ArticleParser(splitArticle);
        Article parsedArticle = parser.getParsedArticle();
        System.out.println(parsedArticle);
    }


    @Test
    public void summarizerTest() throws Exception {
        String title = "Society";
        JSONObject json = api.getArticleJSON(title);
        ArticleSplitter splitter = new ArticleSplitter(json);
        Article splitArticle = splitter.getSplitArticle();

        ArticleParser parser = new ArticleParser(splitArticle);
        Article parsedArticle = parser.getParsedArticle();

        ArticleSummarizer summarizer = new ArticleSummarizer(parsedArticle);
        Article summarizedArticle = summarizer.getSummarizedArticle(0.2f);

        System.out.println(summarizedArticle);
    }

}
