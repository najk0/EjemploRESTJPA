import html.HtmlSanitizer;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class HTMLSanitizerTest {

    private static HtmlSanitizer sanitizer;


    @BeforeClass
    public static void init(){
        sanitizer = new HtmlSanitizer();
    }

    @Test
    public void simpleParsedText(){
        String html = "Texto de prueba <a href=\"link.com\">LINK</a> de correspondencia de links";
        String goalHtml = "Texto de prueba <a href=\"link.com\">LINK</a> de correspondencia de links";

        assertEquals(goalHtml, sanitizer.sanitize(html));
    }

    @Test
    public void complexParsedText(){
        String goalHtml = "<a href=\"/wiki/Information_society\" title=\"Information society\">Information society</a>\n" +
                "<p>Although the concept of <a href=\"/wiki/Information_society\" title=\"Information society\">information society</a> has been under discussion since the 1930s, in the modern world it is almost always applied to the manner in which information technologies have impacted society and culture. It therefore covers the effects of computers and telecommunications on the home, the workplace, schools, government, and various communities and organizations, as well as the emergence of new social forms in cyberspace.[8]</p>\n" +
                "<p>One of the <a href=\"/wiki/European_Union\" title=\"European Union\">European Union</a>'s areas of interest is the information society. Here policies are directed towards promoting an open and competitive <a href=\"/wiki/Digital_economy\" title=\"Digital economy\">digital economy</a>, research into <a href=\"/wiki/Information_and_communication_technologies\" title=\"Information and communication technologies\">information and communication technologies</a>, as well as their application to improve <a href=\"/wiki/Social_inclusion\" title=\"Social inclusion\">social inclusion</a>, <a href=\"/wiki/Public_services\" title=\"Public services\">public services</a>, and <a href=\"/wiki/Quality_of_life\" title=\"Quality of life\">quality of life</a>.[9]</p>\n" +
                "<p>The <a href=\"/wiki/International_Telecommunications_Union\" title=\"International Telecommunications Union\">International Telecommunications Union</a>'s <a href=\"/wiki/World_Summit_on_the_Information_Society\" title=\"World Summit on the Information Society\">World Summit on the Information Society</a> in Geneva and Tunis (2003 and 2005) has led to a number of policy and application areas where action is envisaged.[10]</p>\n" +
                "Knowledge[<a href=\"/w/index.php?title=Society&amp;action=edit&amp;section=17\" title=\"Edit section: Knowledge\">edit</a>]\n";
        String html = "<a href=\"/wiki/Information_society\" title=\"Information society\">Information society</a></div>\n<p>Although the concept of <a href=\"/wiki/Information_society\" title=\"Information society\">information society</a> has been under discussion since the 1930s, in the modern world it is almost always applied to the manner in which information technologies have impacted society and culture. It therefore covers the effects of computers and telecommunications on the home, the workplace, schools, government, and various communities and organizations, as well as the emergence of new social forms in cyberspace.<sup id=\"cite_ref-8\" class=\"reference\">[8]</sup></p>\n<p>One of the <a href=\"/wiki/European_Union\" title=\"European Union\">European Union</a>'s areas of interest is the information society. Here policies are directed towards promoting an open and competitive <a href=\"/wiki/Digital_economy\" title=\"Digital economy\">digital economy</a>, research into <a href=\"/wiki/Information_and_communication_technologies\" class=\"mw-redirect\" title=\"Information and communication technologies\">information and communication technologies</a>, as well as their application to improve <a href=\"/wiki/Social_inclusion\" class=\"mw-redirect\" title=\"Social inclusion\">social inclusion</a>, <a href=\"/wiki/Public_services\" class=\"mw-redirect\" title=\"Public services\">public services</a>, and <a href=\"/wiki/Quality_of_life\" title=\"Quality of life\">quality of life</a>.<sup id=\"cite_ref-9\" class=\"reference\">[9]</sup></p>\n<p>The <a href=\"/wiki/International_Telecommunications_Union\" class=\"mw-redirect\" title=\"International Telecommunications Union\">International Telecommunications Union</a>'s <a href=\"/wiki/World_Summit_on_the_Information_Society\" title=\"World Summit on the Information Society\">World Summit on the Information Society</a> in Geneva and Tunis (2003 and 2005) has led to a number of policy and application areas where action is envisaged.<sup id=\"cite_ref-10\" class=\"reference\">[10]</sup></p>\n<h3><span class=\"mw-headline\" id=\"Knowledge\">Knowledge</span><span class=\"mw-editsection\"><span class=\"mw-editsection-bracket\">[</span><a href=\"/w/index.php?title=Society&amp;action=edit&amp;section=17\" title=\"Edit section: Knowledge\">edit</a><span class=\"mw-editsection-bracket\">]</span></span></h3>\n<div role=\"note\" class=\"hatnote navigation-not-searchable\">";

        assertEquals(goalHtml, sanitizer.sanitize(html));
    }
}
