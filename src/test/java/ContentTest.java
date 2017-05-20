import api.WikiAPI;
import article.ArticleParser;
import article.ArticleSplitter;
import data.Article;
import data.Content;
import data.Section;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class ContentTest {

    private static WikiAPI api;

    @BeforeClass
    public static void init() {
        api = new WikiAPI();
    }


    private void assertConsistency(String html) {
        Content content = new Content(html);
        String plain = content.getPlain();

        //content.debug();

        // El nº de carácteres del texto plano resultante y el parseado deben ser iguales
        assertEquals(plain.length(), content.getPlain().length());

        // La suma de las dos partes (tags y texto plano) debe dar lugar al nº de caracteres original
        assertEquals(html.length(), content.getOnlyTags().length() + content.getOnlyText().length());

        // Dividir el contenido y juntarlo de nuevo no lo altera
        assertEquals(html, content.getAll());

        // El proceso de pasarlo a texto plano debe ser correcto
        assertEquals(plain, content.getPlain());

        // La conversión de texto plano a HTML debe ser correcta
        assertEquals(html, content.getAll(plain));
    }

    @Test
    public void consistencyTestsExample() {
        String html = "<p>Societies may also be structured <a href=\"/wiki/Politics\" title=\"Politics\">politically</a>. In order of increasing size and complexity, there are <a href=\"/wiki/Band_society\" title=\"Band society\">bands</a>, <a href=\"/wiki/Tribe\" title=\"Tribe\">tribes</a>, <a href=\"/wiki/Chiefdom\" title=\"Chiefdom\">chiefdoms</a>, and <a href=\"/wiki/State_(polity)\" title=\"State (polity)\">state</a> societies.";
        String plain = "Societies may also be structured politically. In order of increasing size and complexity, there are bands, tribes, chiefdoms, and state societies.";
        Content content = new Content(html);

        content.debug();

        // El nº de carácteres del texto plano resultante y el parseado deben ser iguales
        assertEquals(plain.length(), content.getPlain().length());

        // La suma de las dos partes (tags y texto plano) debe dar lugar al nº de caracteres original
        assertEquals(html.length(), content.getOnlyTags().length() + content.getOnlyText().length());

        // Dividir el contenido y juntarlo de nuevo no lo altera
        assertEquals(html, content.getAll());

        // El proceso de pasarlo a texto plano debe ser correcto
        assertEquals(plain, content.getPlain());

        // Pasar de texto plano a HTML debe funcionar para frases enteras...
        assertEquals("<p>Societies may also be structured <a href=\"/wiki/Politics\" title=\"Politics\">politically</a>.", content.getAll("Societies may also be structured politically."));

        // ... y también si la frase se queda a medias
        assertEquals("<p>Societies may also be structured <a href=\"/wiki/Politics\" title=\"Politics\">politically</a>. In order of increasing size and complexity, there are <a href=\"/wiki/Band_society\" title=\"Band society\">bands</a>, <a href=\"/wiki/Tribe\" title=\"Tribe\">tribes</a>, <a href=\"/wiki/Chiefdom\" title=\"Chiefdom\">chiefdoms</a>, and", content.getAll("Societies may also be structured politically. In order of increasing size and complexity, there are bands, tribes, chiefdoms, and"));
    }

    @Test
    public void generalTest() throws Exception {
        String title = "Queen (band)";
        JSONObject json = api.getArticleJSON(title);

        ArticleSplitter splitter = new ArticleSplitter(json);
        Article splitArticle = splitter.getSplitArticle();

        ArticleParser parser = new ArticleParser(splitArticle);
        Article parsedArticle = parser.getParsedArticle();

        for(Section section : parsedArticle.getSections()) {
            String sectionHtml = section.getDisplayedContent();
            Content content = new Content(sectionHtml);
            assertConsistency(sectionHtml);
        }
       /*System.out.println("HTML ORIGINAL: " + sectionHtml);
        System.out.println("Html: " + content.getHtml().substring(0, 500));
        System.out.println("Plain: " + content.getOnlyText().substring(0, 500));
        System.out.println("GETALL:\n" + content.getAll("Societies may also be structured politically. In order of increasing size and complexity, there are bands, tribes, chiefdoms, and state "));*/
    }

    @Test
    public void test() {
        String html = "<p>Societies may also be structured <a href=\"/wiki/Politics\" title=\"Politics\">politically</a>. In order of increasing size and complexity, there are <a href=\"/wiki/Band_society\" title=\"Band society\">bands</a>, <a href=\"/wiki/Tribe\" title=\"Tribe\">tribes</a>, <a href=\"/wiki/Chiefdom\" title=\"Chiefdom\">chiefdoms</a>, and <a href=\"/wiki/State_(polity)\" title=\"State (polity)\">state</a> societies.";
        String plain = "Societies may also be structured politically. In order of increasing size and complexity, there are bands, tribes, chiefdoms, and state societies.";
        Content content = new Content(html);
        content.debug();
        String asd = content.getAll("politically. In order of increasing size and complexity, there are bands, tribes, chiefdoms");
        assertEquals("<a href=\"/wiki/Politics\" title=\"Politics\">politically</a>. In order of increasing size and complexity, there are <a href=\"/wiki/Band_society\" title=\"Band society\">bands</a>, <a href=\"/wiki/Tribe\" title=\"Tribe\">tribes</a>, <a href=\"/wiki/Chiefdom\" title=\"Chiefdom\">chiefdoms</a>", asd);
    }

}
