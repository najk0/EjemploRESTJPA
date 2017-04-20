import api.WikiAPI;
import data.ArticleInfo;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArticleInfoTest {

    private static WikiAPI api;

    @BeforeClass
    public static void init() {
        api = new WikiAPI();
    }

    @Test
    public void articleDoesNotExist() {
        String title = "ArticleWhichDoesNotExist";
        ArticleInfo info = api.getArticleInfo(title);

        assertEquals(false, info.exists());
    }

    @Test
    public void articleRedirects() {
        String title = "sosiety";
        ArticleInfo info = api.getArticleInfo(title);

        assertEquals("Society", info.getRedirectsTo());
        assertEquals(true, info.isRedirection());
        assertEquals(false, info.isDisambiguation());
        assertEquals(false, info.isArticle());
    }

    @Test
    public void articleIsDisambiguation() {
        String title = "Queen";
        ArticleInfo info = api.getArticleInfo(title);

        assertEquals(null, info.getRedirectsTo());
        assertEquals(false, info.isRedirection());
        assertEquals(true, info.isDisambiguation());
        assertEquals(false, info.isArticle());
    }

    @Test
    public void articleIsRegular() {
        String title = "Mass";
        ArticleInfo info = api.getArticleInfo(title);

        assertEquals(true, info.isArticle());
        assertEquals(null, info.getRedirectsTo());
        assertEquals(false, info.isRedirection());
        assertEquals(false, info.isDisambiguation());
    }

}
