import api.WikiAPI;
import data.Article;
import org.junit.BeforeClass;
import org.junit.Test;

public class ArticleTest {

    private static WikiAPI api;

    @BeforeClass
    public static void init() {
        api = new WikiAPI();
    }

    @Test
    public void test() {
        String title = "Mass";
        Article article = api.getArticle(title);
        System.out.println(article);
    }
}
