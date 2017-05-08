package html;

import java.util.*;

public class Tag {

    // Tags sin cierre p.e. <br>
    public static final int TYPE_SINGLE = 0;

    // Tags con cierre en el tag de inicio p.e. <img />
    public static final int TYPE_NOENDTAG = 1;

    // Tags con cierre p.e. <b></b>
    public static final int TYPE_ENDTAG = 2;


    private final String name;

    private final int type;

    private List<HTMLAttribute> attrs;

    private StringBuilder content;


    public Tag(String tagname, int type) {
        if (tagname == null || tagname.isEmpty()) {
            throw new RuntimeException();
        }
        this.name = tagname;
        this.type = type;
        this.content = new StringBuilder();
        attrs = new ArrayList<>();
    }

    public Tag setContent(String content) {
        this.content.append(content);
        return this;
    }

    public Tag setContent(Tag tag) {
        this.content.append(tag.toString());
        return this;
    }

    public Tag addContent(String moreContent) {
        this.content.append(moreContent);
        return this;
    }

    public Tag addContent(Tag anotherTag) {
        this.content.append(anotherTag);
        return this;
    }

    public Tag attr(String attrName, String attrValue) {
        attrs.add(new HTMLAttribute(attrName, attrValue));
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Abrimos el tag y añadimos el nombre del tag p.e. <a
        sb.append("<").append(name);
        // Introducimos los atributos p.e. href="wikipedia.org"
        for(HTMLAttribute attr : attrs) {
            String attrName = attr.name;
            String attrValue = attr.value;
            sb.append(" ").append(attrName);
            if (attrValue != null && attrValue.isEmpty() == false) {
                sb.append("=\"").append(attrValue).append("\"");
            }
        }
        // Dependiendo del tipo de tag, introducimos el contenido y/o el cierre
        switch(type) {
            case TYPE_SINGLE:
                sb.append(">");
                break;
            case TYPE_NOENDTAG:
                sb.append(" />");
                break;
            case TYPE_ENDTAG:
                sb.append(">").append(content).append("</").append(name).append(">");
                break;
            default:
                throw new RuntimeException();
        }
        return sb.toString();
    }

}


class HTMLAttribute {

    String name;

    String value;

    public HTMLAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
