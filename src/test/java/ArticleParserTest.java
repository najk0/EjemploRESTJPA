import api.WikiAPI;
import article.ArticleParser;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


public class ArticleParserTest {

    private static WikiAPI api;


    @BeforeClass
    public static void init() {
        api = new WikiAPI();
    }

    @Test
    public void test() {
        JSONObject json = api.getArticleJSON("Society");
        ArticleParser ab = new ArticleParser(json);
        String sectionHTML = ab.getSectionHTML(22);
        System.out.println(sectionHTML);
        assertEquals("", "");
    }


    @Test
    public void getAllSectionHTMLEqualsFullHTML() {
        JSONObject json = api.getArticleJSON("Society");
        ArticleParser ab = new ArticleParser(json);

        StringBuilder allSectionsHTML = new StringBuilder();
        for(int i = 0; i < ab.getSize(); i++) {
            allSectionsHTML.append(ab.getSectionHTML(i));
        }
        assertEquals(ab.getHTML(), allSectionsHTML.toString());
    }

    @Test
    public void getSectionsHTML() {
        JSONObject json = api.getArticleJSON("Society");
        ArticleParser ab = new ArticleParser(json);
        System.out.println(ab.getSectionsHTML());
    }

    @Test
    public void getContentHTML() {
        JSONObject json = api.getArticleJSON("Society");
        ArticleParser ab = new ArticleParser(json);
        System.out.println(ab.getContentHTML());
    }

}
