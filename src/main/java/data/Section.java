package data;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Section {

    /* Definición de la sección inicial o cabecera, antes de todas
    las demás secciones, inicializada por defecto con esta información. */
    public static final Section HEADER = new Section(RawSection.HEADER);

    private RawSection rawSection;

    @XmlAttribute
    private String anchor;

    @XmlAttribute
    private String name;

    @XmlAttribute
    private int index; // p.e. 3

    @XmlAttribute
    private String number; // p.e 1.3

    @XmlAttribute
    private int depth;

    @XmlAttribute
    private String content;


    public Section() {
        super();
    }

    public Section(RawSection rs) {
        this();
        this.rawSection = rs;
        this.anchor = rs.getAnchor();
        this.index = Integer.parseInt(rs.getIndex());
        this.number = rs.getNumber();
        this.depth = Integer.parseInt(rs.getLevel());
        this.name = rs.getLine();
    }

    public Section(RawSection rs, String content) {
        this(rs);
        setContent(content);
    }

    public RawSection getRawSection() {
        return rawSection;
    }

    public String getAnchor() {
        return anchor;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public String getNumber() {
        return number;
    }

    public int getDepth() {
        return depth;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        // El índice que determina el orden de las secciones lo más a la izquierda posible
        sb.append("(").append(index).append(") ");
        // Indentamos las subsecciones
        for(int i = 0; i < depth; i++) {
            sb.append("\t");
        }
        // Mostramos el número (p.e. 1.3) y su nombre
        sb.append(number).append(" - ").append(name).append(":\n");
        // Y el contenido en líneas de X caracteres
        int charsPerLine = 80;
        int startIndex = 0;
        int endIndex;
        while (startIndex < content.length()) {
            endIndex = Math.min(startIndex + charsPerLine, content.length());
            sb.append("\t").append(content.substring(startIndex, endIndex)).append("\n");
            startIndex += charsPerLine;
        }
        sb.append("\n");
        return sb.toString();
    }

}
