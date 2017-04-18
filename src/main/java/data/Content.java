package data;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Content {

    @XmlElement
    private String rawHTML;

    @XmlElement
    private String plaintext;

    @XmlElementWrapper
    @XmlElement(name="p")
    private List<Paragraph> paragraphs; // TODO mejorar


    public Content() {
        super();
        rawHTML = "NULL";
        plaintext = "NULL";
        paragraphs = new ArrayList<>();
    }

    public Content(String html) {
        this();
        this.rawHTML = html;
        this.paragraphs = getParagraphs(html);
        this.plaintext = getAllPlainText(paragraphs);
    }

    public String getRawHTML() {
        return rawHTML;
    }

    public String getPlaintext() {
        return plaintext;
    }

    private List<Paragraph> getParagraphs(String html) {
        List<Paragraph> paragraphs = new ArrayList<>();
        Source src = new Source(html);
        List<Element> allP = src.getAllElements(HTMLElementName.P);

        for(Element p : allP) {
            Paragraph para = new Paragraph(p);
            if (para.hasContent()) {
                paragraphs.add(para);
            }
        }
        return paragraphs;
    }

    // Devuelve el texto que contienen todos los párrafos en texto plano
    private String getAllPlainText(List<Paragraph> paragraphList) {
        StringBuilder sb = new StringBuilder();
        for(Paragraph p : paragraphList) {
            sb.append(p.getPlain()).append("\n\n");
        }
        return sb.toString();
    }

}

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
class Paragraph {

    @XmlElement
    private String html;

    @XmlElement
    private String plain;


    public Paragraph() {
        super();
        html = "NULL";
        plain = "NULL";
    }

    public Paragraph(Element p) {
        this();
        this.html = p.toString();
        this.plain = p.getTextExtractor().toString();
    }

    // TODO mejorar este método, cuando conozcamos qué contenido suele haber en etiquetas <p> inútiles
    public boolean hasContent() {
        return plain != null && plain.isEmpty() == false;
    }

    public String getHtml() {
        return html;
    }

    public String getPlain() {
        return plain;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Paragraph HTML: \n");
        sb.append(this.html).append("\n");
        sb.append("------------------------------\n");
        sb.append("Plain text: \n");
        sb.append(this.plain).append("\n");
        sb.append("------------------------------\n");

        return sb.toString();
    }
}