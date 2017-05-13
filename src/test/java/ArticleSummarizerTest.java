import api.WikiAPI;
import article.ArticleParser;
import article.ArticleSplitter;
import article.ArticleSummarizer;
import data.Article;
import data.Section;
import data.Sections;
import net.sf.classifier4J.summariser.SimpleSummariser;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ArticleSummarizerTest {

    private static WikiAPI api;

    private static ArticleSummarizer summarizer;

    private static SimpleSummariser classifier4JSummariser;

    @BeforeClass
    public static void init() {
        api = new WikiAPI();
        classifier4JSummariser = new SimpleSummariser();
        JSONObject json = api.getArticleJSON("Society");
        ArticleSplitter splitter = new ArticleSplitter(json);
        Article splitArticle = splitter.getSplitArticle();
        ArticleParser parser = new ArticleParser(splitArticle);
        Article parsedArticle = parser.getParsedArticle();
        summarizer = new ArticleSummarizer(parsedArticle);
    }

    @Test
    public void splitInLines() {
        Sections sections = summarizer.getArticle().getSections();
        for(Section section : sections) {
            String html = section.getContent();
            List<String> lines = summarizer.splitInLines(html);
            // List<String> lines = new Article(null, null).spliter(html); // Método de Iulian, no funciona correctamente
            System.out.println("Section " + section.getNumber() + " has " + lines.size() + " lines:");
            int i = 1;
            for(String line : lines) {
                System.out.println("(" + i++ + ")\t" + line);
            }
            System.out.println("------------------------------\n");
        }
    }

    @Test
    public void getAllLines() {
        List<String> allLines = summarizer.getAllLines();
        int i = 1;
        for(String line: allLines) {
            System.out.println("(" + i++ + "/" + allLines.size() + ") " + line);
        }
    }

    @Test
    public void getAllLinesPlaintext() {
        // Obtenemos todas las líneas de los párrafos <p> en texto plano
        List<String> allLines = summarizer.getAllLinesPlaintext();
        int i = 1;
        for(String line: allLines) {
            System.out.println("(" + i++ + "/" + allLines.size() + ") " + line);
        }
        // Unimos todas las líneas para poder resumir el texto.
        String articleContent = summarizer.joinLines(allLines);
        // Classifier4J agrega un punto al final del documento si éste no termina con uno.
        // Si el texto original no contiene un punto al final, tras el resumen se
        // considerará que no coinciden, sólo porque no terminan ambos en un punto.
        if (articleContent.endsWith(".") == false) {
            articleContent += ".";
        }
        // Resumimos el artículo indicando que queremos obtener el mismo número de líneas
        // que el artículo original, esencialmente no resumiendo nada. Hacemos esto para
        // comprobar que el criterio que tenemos nosotros para separar las frases es el
        // mismo que tiene Classifier4J, ya que de lo contrario no podríamos relacionar
        // las líneas que han quedado resumidas con las originales, porque una línea podría
        // haber sido dividida de dos formas distintas.
        String summary = classifier4JSummariser.summarise(articleContent, allLines.size());
        assertEquals(articleContent, summary);
    }

}
