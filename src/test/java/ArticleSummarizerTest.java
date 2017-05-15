import api.WikiAPI;
import article.ArticleParser;
import article.ArticleSplitter;
import article.ArticleSummarizer;
import data.Article;
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
        JSONObject json = api.getArticleJSON("Queen (band)");
        ArticleSplitter splitter = new ArticleSplitter(json);
        Article splitArticle = splitter.getSplitArticle();
        ArticleParser parser = new ArticleParser(splitArticle);
        Article parsedArticle = parser.getParsedArticle();
        summarizer = new ArticleSummarizer(parsedArticle);
    }


    @Test
    public void compareLineLists() {
        // Artículo original
        // Obtenemos todas las líneas de los párrafos <p> en texto plano
        // Unimos todas las líneas para poder resumir el texto.
        String originalArticleContent = summarizer.stripHtml(summarizer.getAllContent());
        List<String> originalArticleLines = summarizer.splitInLines(originalArticleContent);

        System.out.println("ORIGINAL ARTICLE CONTENT:\n" + originalArticleContent);

        // Artículo resumido
        // Resumimos el artículo indicando que queremos obtener el mismo número de líneas
        // que el artículo original, esencialmente no resumiendo nada. Hacemos esto para
        // comprobar que el criterio que tenemos nosotros para separar las frases es el
        // mismo que tiene Classifier4J, ya que de lo contrario no podríamos relacionar
        // las líneas que han quedado resumidas con las originales, porque una línea podría
        // haber sido dividida de dos formas distintas.
        String summarizedArticleContent = summarizer.summarize(originalArticleContent, originalArticleLines.size());
        List<String> summarizedArticleLines = summarizer.splitInLines(summarizedArticleContent);
        //List<String> summarizedArticleLines = summarizer.splitInLines(originalArticleContent);

        /*
        System.out.println("Original text:\n");
        System.out.println(originalArticleContent);
        System.out.println("\n\n*******************************************\n\n");
        System.out.println("Summarized text:\n");
        System.out.println(summarizedArticleContent);

        System.out.println("Original lines: " + originalArticleLines.size());
        System.out.println("Summarized lines: " + summarizedArticleLines.size());
        */
        assertAllFromList2AreInList1(originalArticleLines, summarizedArticleLines);
    }

    private boolean assertAllFromList2AreInList1(List<String> list1, List<String> list2) {
        int list2Index = 0;
        int matches = 0;
        int i = 0;
        for(; i < list1.size(); i++) {
            System.out.println("(" + i + "/" + list1.size() + ")"
                    + "\nO: " + list1.get(i)
                    + "\nS: " + list2.get(list2Index)
                    + "\n----------");
            // Cada vez que encontramos una coincidencia...
            if (list1.get(i).equals(list2.get(list2Index))) {
                matches++;
                list2Index++;
            }
            // Si ya las hemos encontrado todas, paramos.
            if (list2Index == list2.size()) {
                break;
            }
        }
        // Comprobamos si todas las líneas de la segunda lista existen en la primera lista
        assertEquals(list2.size(), matches);
        System.out.println("Las " + list2.size() + " líneas de la segunda lista están contenidas en las " + list1.size() + " líneas de la primera lista. Todas ellas están en las primeras " + i + " líneas de la primera lista.");
        return list2.size() == matches;
    }

}
