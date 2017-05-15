import api.WikiAPI;
import article.ArticleSplitter;
import data.Article;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


public class ArticleSplitterTest {

    private static WikiAPI api;


    @BeforeClass
    public static void init() {
        api = new WikiAPI();
    }

    @Test
    public void test() {
        JSONObject json = api.getArticleJSON("Society");
        ArticleSplitter splitter = new ArticleSplitter(json);
        Article splitArticle = splitter.getSplitArticle();
        System.out.println(splitArticle);
    }


    @Test
    public void getAllSectionHTMLEqualsFullHTML() {
        JSONObject json = api.getArticleJSON("Society");
        ArticleSplitter ab = new ArticleSplitter(json);

        StringBuilder allSectionsHTML = new StringBuilder();
        for(int i = 0; i < ab.getSize(); i++) {
            allSectionsHTML.append(ab.getSectionHTML(i));
        }
        assertEquals(ab.getHTML(), allSectionsHTML.toString());
    }

    @Test
    public void getSectionsHTML() {
        JSONObject json = api.getArticleJSON("Society");
        ArticleSplitter ab = new ArticleSplitter(json);
        System.out.println(ab.getSectionsHTML());
    }

    @Test
    public void getContentHTML() {
        JSONObject json = api.getArticleJSON("Society");
        ArticleSplitter ab = new ArticleSplitter(json);
        System.out.println(ab.getContentHTML());
    }

}
