import api.WikiAPI;
import article.ArticleParser;
import article.ArticleSplitter;
import data.Article;
import data.Section;
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
        String title = "Wicket gate";
        JSONObject json = api.getArticleJSON(title);
        ArticleSplitter splitter = new ArticleSplitter(json);
        Article splitArticle = splitter.getSplitArticle();
        ArticleParser parser = new ArticleParser(splitArticle);
        Article parsedArticle = parser.getParsedArticle();
        System.out.println(parsedArticle);
    }
}
