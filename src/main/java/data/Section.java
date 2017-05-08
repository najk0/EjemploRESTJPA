package data;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Section {

    /* Definición de la sección inicial o cabecera, antes de todas
las demás secciones, inicializada por defecto con esta información. */
    public static final Section HEADER = new Section(RawSection.HEADER);

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

    @XmlElement
    private List<String> paragraphs;


    public Section() {
        super();
    }

    public Section(RawSection rs) {
        this();
        this.anchor = rs.getAnchor();
        this.index = Integer.parseInt(rs.getIndex());
        this.number = rs.getNumber();
        this.depth = Integer.parseInt(rs.getLevel());
        this.name = rs.getLine();
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
        for(String p : paragraphs) {
            p.replace("\n", ""); // Eliminamos cualquier salto de línea para mejorar la legibilidad
            // Indentamos el contenido de cada sección
            for(int i = 0; i < depth + 3; i++) {
                sb.append("\t");
            }
            // Limitamos el contenido que se muestra (sólo el principio)
            sb.append(p.substring(0, Math.min(p.length(), 80)));
            if (p.length() > 80) sb.append("...");
            sb.append("\n");
        }
        return sb.toString();
    }

}
