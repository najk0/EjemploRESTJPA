import api.WikiAPI;
import data.Article;
import org.json.JSONObject;
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
        //String title = "-1"; // Wicket gate
        //JSONObject json = api.getArticleJSON(title);
        //System.out.println(json.toString(2));
    }
}
