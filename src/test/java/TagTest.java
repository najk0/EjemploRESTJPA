import html.Tag;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TagTest {


    @Test
    public void tagtype_simple() {
        Tag t = new Tag("br", Tag.TYPE_SINGLE);
        assertEquals("<br>", t.toString());
    }

    @Test
    public void tagtype_noendtag() {
        Tag t = new Tag("img", Tag.TYPE_NOENDTAG)
                .attr("src", "/img/pic.jpg")
                .content("Should not appear");
        assertEquals("<img src=\"/img/pic.jpg\" />", t.toString());
    }

    @Test
    public void tagtype_endtag() {
        Tag t = new Tag("a", Tag.TYPE_ENDTAG)
                .attr("href", "wikipedia.org")
                .content("Click aquí");
        assertEquals("<a href=\"wikipedia.org\">Click aquí</a>", t.toString());
    }

    @Test
    public void many_attributes() {
        Tag t = new Tag("a", Tag.TYPE_ENDTAG)
                .attr("href", "wikipedia.org")
                .attr("title", "To Wikipedia.org")
                .attr("style", "font-size: 14pt; font-weight: bold; color: #F0F;")
                .content("Click aquí");
        assertEquals("<a href=\"wikipedia.org\" title=\"To Wikipedia.org\" style=\"font-size: 14pt; font-weight: bold; color: #F0F;\">Click aquí</a>", t.toString());
    }

    @Test
    public void only_content() {
        Tag t = new Tag("b", Tag.TYPE_ENDTAG)
                .content("BOLD TEXT");
        assertEquals("<b>BOLD TEXT</b>", t.toString());
    }

    @Test
    public void tag_inside_tag() {
        Tag b = new Tag("b", Tag.TYPE_ENDTAG)
                .content("BOLD TEXT");
        Tag p = new Tag("p", Tag.TYPE_ENDTAG)
                .content("Important content should go in " + b.toString());
        assertEquals("<p>Important content should go in <b>BOLD TEXT</b></p>", p.toString());
    }

}
