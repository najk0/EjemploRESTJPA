import html.LinkToTextComparator;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class LinkToTextComparatorTest {

    private static LinkToTextComparator lttc;
    private String text	= "Texto de prueba LINK de correspondencia de links";

    @BeforeClass
    public static void init(){
        lttc = new LinkToTextComparator();
    }

    @Test
    public void oneLink(){
        String textHTML = "Texto de prueba <a href=\"link.com\">LINK</a> de correspondencia de links";

        assertEquals(true, lttc.compareLinkWithText(textHTML, text));
    }

    @Test
    public void TwoLinks(){
        String textHTML = "Texto de prueba <a href=\"link.com\">LINK</a> de correspondencia de links <a></a>";
        String textHTML2= "<a></a>Texto de prueba <a href=\"link.com\">LINK</a> de correspondencia de links";
        assertEquals(true, lttc.compareLinkWithText(textHTML, text));
        assertEquals(true, lttc.compareLinkWithText(textHTML2, text));
    }

    @Test
    public void TwoConsecutiveLinks(){
        String textHTML = "Texto de prueba <a href=\"link.com\">LINK</a> de <a></a><a></a>correspondencia de links";
        assertEquals(true, lttc.compareLinkWithText(textHTML, text));
    }


}
