package data;

import data.HtmlSanitizer.*;

import net.htmlparser.jericho.*;

import java.util.ArrayList;
import java.util.List;

public class HTMLParser {

    private String html = "";
    private Source source;
    private String plainText = "";
    private List<String> links;

    public HTMLParser(String html) {
        super();
        this.html = html;
        this.links = new ArrayList<String>();
        this.source = new Source(this.html);
    }

    public String parseHTML() {
        this.plainText = HtmlSanitizer.sanitize(this.source);

        return this.plainText;
    }
}
