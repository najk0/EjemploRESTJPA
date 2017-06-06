import api.WikiAPI;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class WikiAPITest {

    private static WikiAPI api;

    @BeforeClass
    public static void init() {
        api = new WikiAPI();
    }

    @Test
    public void articleDoesNotExist() {
        String[] titles = new String[] {
                null,
                "",
                "ArticleWhichDoesNotExist"
        };
        for(String title: titles) {
            JSONObject json = api.getArticleJSON(title);
            System.out.println(json.toString(2));
            if (json.has("error")) {
                String errorcode = json.getJSONObject("error").getString("code");
                boolean errorcodeIsValid =
                        "missingtitle".equals(errorcode) ||
                                "invalidtitle".equals(errorcode);
                assertEquals(true, errorcodeIsValid);

            } else if (json.has("warnings")) {
                // Es posible que se den otro tipo de errores no tan específicos como
                // el que hemos indicado en la condición superior, pero los damos por válidos.

            } else  {
                fail();
            }
        }
    }


    @Test
    public void isValidLink_true() {
        boolean testFailed = false;
        String[] keywords = new String[] {
                "wikipedia.org/wiki/Mass",
                "en.wikipedia.org/wiki/Mass",
                "http://wikipedia.org/wiki/Mass",
                "http://en.wikipedia.org/wiki/Mass",
                "https://wikipedia.org/wiki/Mass",
                "https://en.wikipedia.org/wiki/Mass",
        };
        for(String keyword : keywords) {
            boolean isLink = api.isValidLink(keyword);
            System.out.println("\"" + keyword + "\" is " + (isLink ? "" : "NOT") + " a valid link");
            if (isLink == false) {
                testFailed = true;
            }
        }
        if (testFailed) {
            fail();
        }
    }


    @Test
    public void isValidLink_false() {
        boolean testFailed = false;
        String[] keywords = new String[] {
                // Valores nulos o vacíos
                null,
                "",
                // Mal host
                "Qwikipedia.org/wiki/Mass",
                "en.Qwikipedia.org/wiki/Mass",
                // Mal host http
                "http://Qwikipedia.org/wiki/Mass",
                "http://en.Qwikipedia.org/wiki/Mass",
                "https://Qwikipedia.org/wiki/Mass",
                "https://en.Qwikipedia.org/wiki/Mass",
                // Fallo en la ruta REST
                "wikipedia.org/whoops/Mass",
        };
        for(String keyword : keywords) {
            boolean isLink = api.isValidLink(keyword);
            System.out.println("\"" + keyword + "\" is" + (isLink ? "" : " NOT") + " a valid link");
            if (isLink == true) {
                testFailed = true;
            }
        }
        if (testFailed) {
            fail();
        }
    }


    @Test
    public void getTitleFromLink_ok() {
        boolean testFailed = false;
        String[] links = new String[] {
                "http://wikipedia.org/wiki/Mass",
                "https://en.wikipedia.org/wiki/St_Francis_Gaels_GAA,_Cabinteely",
                "https://en.wikipedia.org/wiki/Barnes%27_astrapia",
                "https://en.wikipedia.org/wiki/Ḑ",
        };
        String[] titles = new String[] {
                "Mass",
                "St_Francis_Gaels_GAA,_Cabinteely",
                "Barnes%27_astrapia",
                "Ḑ",
        };
        if (links.length != titles.length) {
            System.err.println("No hay el mismo número de entradas en los links y títulos.");
            fail();
        }
        for(int i = 0; i < links.length; i++) {
            String link = links[i];
            String title = titles[i];
            String linkTitle = api.getTitleAnchorFromLink(link);
            boolean isTitleCorrect = title.equals(linkTitle);
            System.out.println("\"" + title + "\" does" + (isTitleCorrect ? "" : " NOT") + " match link title \"" + linkTitle + "\" for link \"" + link + "\"");
            if (!isTitleCorrect) {
                testFailed = true;
            }
        }
        if (testFailed) {
            fail();
        }
    }

}
