
import ie.dcu.cngl.summarizer.Aggregator;
import ie.dcu.cngl.summarizer.Summarizer;
import ie.dcu.cngl.summarizer.Weighter;
import ie.dcu.cngl.tokenizer.Structurer;
import junit.framework.TestCase;
import net.sf.classifier4J.summariser.SimpleSummariser;

import java.io.File;

import org.apache.commons.io.FileUtils;

public class SumarizerTest {

    public static void main(String[] sd)throws Exception {
        String text = FileUtils.readFileToString(new File("C:\\articulo.txt"), "UTF-8");
        SimpleSummariser summariser = new SimpleSummariser();
        String input = "Classifier4J is a java package for working with text. Classifier4J includes a summariser. A Summariser allows the summary of text. A Summariser is really cool. I don't think there are any other java summarisers.";
        String result = summariser.summarise(text , 2);
        System.out.println(result);
    }
}