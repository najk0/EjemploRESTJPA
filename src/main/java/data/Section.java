package data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Section {

    private String name;

    private int index; // p.e. 3

    private String number; // p.e 1.3

    private int depth;

    private String content; // Texto que contiene la sección


    public Section() {
        super();
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        // Imprimimos el índice que determina el orden de las secciones
        sb.append(index).append(" ");
        // Indentamos las subsecciones
        for(int i = 0; i < depth; i++) {
            sb.append("\t");
        }
        // Mostramos el número (p.e. 1.3), su nombre...
        sb.append(number).append(" - ").append(name).append(":\n");
        // Y si lo tenemos, parte del contenido
        if (content != null) {
            sb.append("\t").append(content.substring(0, Math.min(content.length(), 150)));
            if (content.length() > 150) sb.append("...");
            sb.append("\n");
        }

        return sb.toString();
    }
}
