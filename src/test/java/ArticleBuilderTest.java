import api.WikiAPI;
import data.ArticleBuilder;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArticleBuilderTest {

    private static WikiAPI api;


    @BeforeClass
    public static void init() {
        api = new WikiAPI();
    }

    @Test
    public void test() {
        JSONObject json = api.getArticleJSON("Society");
        ArticleBuilder ab = new ArticleBuilder(json);
        String sectionHTML = ab.getSectionHTML(22);
        System.out.println(sectionHTML);
        assertEquals("", "");
    }


    @Test
    public void getAllSectionHTMLEqualsFullHTML() {
        JSONObject json = api.getArticleJSON("Society");
        ArticleBuilder ab = new ArticleBuilder(json);

        StringBuilder allSectionsHTML = new StringBuilder();
        for(int i = 0; i < ab.getSize(); i++) {
            allSectionsHTML.append(ab.getSectionHTML(i));
        }
        assertEquals(ab.getHTML(), allSectionsHTML.toString());
    }

}
