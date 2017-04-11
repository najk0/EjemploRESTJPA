import api.StructuredArticle;
import api.WikiAPI;
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
        String title = "Mass"; // Wicket gate
        StructuredArticle strArt = api.getStructuredArticle(title);
    }
}
