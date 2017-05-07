
import data.Article;

import net.sf.classifier4J.summariser.SimpleSummariser;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class SplitTest {


    String text1 = "Hay controversia sobre su fiabilidad y precisión.18 La revista científica" +
            " Nature declaró en ?diciembre de 2005 que la Wikipedia. en inglés.... era casi tan exacta en " +
            "artículos científicos! como la Encyclopaedia Britannica.19 El estudio .   se realizó " +
            "comparando 42.5 artículos de.5 ambas obras por un comité de. expertos sin." +
            " que estos supieran " +
            "de cuál de. las dos enciclopedias provenían. ";

    String testIn1 = "";
    String testIn2 = ".";
    String testIn3 = "";
    String testIn4 = "";
    String testIn5 = "";
    String testIn6 = "";
    String testIn7 = "";

    Article article = new Article();


    @Test
    public void test() throws Exception{

        String text = FileUtils.readFileToString(new File("C:\\articulo.txt"), "UTF-8");
        SimpleSummariser summariser = new SimpleSummariser();
        String result = summariser.summarise(text , 2);

        Assert.assertTrue(article.spliter("").size()==0);
        Assert.assertTrue(article.spliter("    ").size()==0);
        Assert.assertTrue(article.spliter(".").size()==0);
        Assert.assertTrue(article.spliter("..").size()==1);
        Assert.assertTrue(article.spliter("..").get(0).equals(".."));
        Assert.assertTrue(article.spliter("...").size()==1);
        Assert.assertTrue(article.spliter("...").get(0).equals("..."));
        Assert.assertTrue(article.spliter(".    .").size()==0);
        Assert.assertTrue(article.spliter("  .   .  ").size()==0);

        Assert.assertTrue(article.spliter("a").size()==1);
        Assert.assertTrue(article.spliter("a").get(0).equals("a"));

        Assert.assertTrue(article.spliter("a.").size()==1);
        Assert.assertTrue(article.spliter("a.").get(0).equals("a."));

        Assert.assertTrue(article.spliter(".a").size()==1);
        Assert.assertTrue(article.spliter(".a").get(0).equals("a"));

        Assert.assertTrue(article.spliter(".a.").size()==1);
        Assert.assertTrue(article.spliter(".a.").get(0).equals("a."));

        Assert.assertTrue(article.spliter(".a .").size()==1);
        Assert.assertTrue(article.spliter(".a .").get(0).equals("a ."));

        Assert.assertTrue(article.spliter(".a. ").size()==1);
        Assert.assertTrue(article.spliter(".a. ").get(0).equals("a."));

        Assert.assertTrue(article.spliter(".a.b").size()==2);
        Assert.assertTrue(article.spliter(".a.b").get(0).equals("a."));
        Assert.assertTrue(article.spliter(".a.b").get(1).equals("b"));

        Assert.assertTrue(article.spliter(".a.b.").size()==2);
        Assert.assertTrue(article.spliter(".a.b.").get(0).equals("a."));
        Assert.assertTrue(article.spliter(".a.b.").get(1).equals("b."));

        Assert.assertTrue(article.spliter("5.2b.").size()==1);
        Assert.assertTrue(article.spliter("5.2b.").get(0).equals("5.2b."));

        Assert.assertTrue(article.spliter("aaa. cc5.2b.").size()==2);
        Assert.assertTrue(article.spliter("aaa. cc5.2b.").get(0).equals("aaa."));
        Assert.assertTrue(article.spliter("aaa. cc5.2b.").get(1).equals(" cc5.2b."));

        Assert.assertTrue(article.spliter("aaa... cc5.2b.").size()==2);
        Assert.assertTrue(article.spliter("aaa... cc5.2b.").get(0).equals("aaa..."));
        Assert.assertTrue(article.spliter("aaa... cc5.2b.").get(1).equals(" cc5.2b."));

        Assert.assertTrue(article.spliter("aaa..cc5.2b.").size()==1);
        Assert.assertTrue(article.spliter("aaa..cc5.2b.").get(0).equals("aaa..cc5.2b."));

        Assert.assertTrue(article.spliter("aaa? cc5.2b.").size()==2);
        Assert.assertTrue(article.spliter("aaa? cc5.2b.").get(0).equals("aaa?"));
        Assert.assertTrue(article.spliter("aaa? cc5.2b.").get(1).equals(" cc5.2b."));

        Assert.assertTrue(article.spliter("aaa! cc5.2b.").size()==2);
        Assert.assertTrue(article.spliter("aaa! cc5.2b.").get(0).equals("aaa!"));
        Assert.assertTrue(article.spliter("aaa! cc5.2b.").get(1).equals(" cc5.2b."));


        System.out.println("_________________________");

        System.out.println("resultado de sumarizer:");
        System.out.println(result);
        System.out.println("_________________________");
        Assert.assertTrue(article.spliter(result).size()>0);


    }
}
