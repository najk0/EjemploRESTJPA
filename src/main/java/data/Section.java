package data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Section {

    /* Definición de la sección inicial o cabecera, antes de todas
    las demás secciones, inicializada por defecto con esta información. */
    public static final Section HEADER = new Section();
    static {
        HEADER.setName("Introduction");
        HEADER.setDepth(2);
        HEADER.setNumber("0");
        HEADER.setIndex(0);
        HEADER.setAnchor("Introduction");
    }

    private String anchor;

    private String name;

    private int index; // p.e. 3

    private String number; // p.e 1.3

    private int depth;

    private Content content; // El contenido en texto plano TODO quitar


    public Section() {
        super();
    }

    public Section(RawSection rs) {
        super();
        this.anchor = rs.getAnchor();
        this.index = Integer.parseInt(rs.getIndex());
        this.number = rs.getNumber();
        this.depth = Integer.parseInt(rs.getLevel());
        this.name = rs.getLine();
    }

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        // Imprimimos el índice que determina el orden de las secciones
        sb.append("(").append(index).append(") ");
        // Indentamos las subsecciones
        for(int i = 0; i < depth; i++) {
            sb.append("\t");
        }
        // Mostramos el número (p.e. 1.3), su nombre...
        sb.append(number).append(" - ").append(name).append(":\n");
        // Y si lo tenemos, parte del contenido
        if (content != null) {
            String contentPlaintext = content.getPlaintext();
            contentPlaintext.replace("\n", ""); // Eliminamos cualquier salto de línea para mejorar la legibilidad
            // Indentamos el contenido de cada sección
            for(int i = 0; i < depth + 3; i++) {
                sb.append("\t");
            }
            sb.append(contentPlaintext.substring(0, Math.min(contentPlaintext.length(), 80)));
            if (contentPlaintext.length() > 80) sb.append("...");
            sb.append("\n");
        }

        return sb.toString();
    }
}
