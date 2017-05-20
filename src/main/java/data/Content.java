package data;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Content {

    private final String html;

    private String plain;

    private List<Elem> elements;


    public Content(String html) {
        this.html = html;
        this.elements = parseElements(html);
        this.plain = getOnlyText();
    }

    public String getHtml() {
        return this.html;
    }

    public String getPlain() {
        return this.plain;
    }

    public String getOnlyText() {
        StringBuilder sb = new StringBuilder();
        for(Elem e : elements) {
            if (e.isText()) {
                sb.append(e);
            }
        }
        return sb.toString();
    }

    public String getOnlyText(String delimiter) {
        StringBuilder sb = new StringBuilder();
        for(Elem e : elements) {
            if (e.isText()) {
                sb.append(e).append(delimiter);
            }
        }
        // Eliminamos el último delimitador
        sb.replace(sb.length() - 1 - delimiter.length(), sb.length() - 1, "");
        return sb.toString();
    }

    public String getOnlyTags() {
        StringBuilder sb = new StringBuilder();
        for(Elem e : elements) {
            if (e.isTag()) {
                sb.append(e);
            }
        }
        return sb.toString();
    }

    public String getAll() {
        StringBuilder sb = new StringBuilder();
        for(Elem e : elements) {
            sb.append(e);
        }
        return sb.toString();
    }

    public String getAll(int beginIndex) {
        StringBuilder sb = new StringBuilder();
        for(Elem e : elements) {
            int elemStart = e.getStart();
            if (elemStart >= beginIndex) {
                sb.append(e);
            }
        }
        return sb.toString();
    }

    public String getAll(int beginIndex, int endIndex) {
        //System.out.println("\nGet all(" + beginIndex + ", " + endIndex + ")");
        StringBuilder sb = new StringBuilder();
        for(Elem e : elements) {
            int offset;
            int elemStart = e.getStart();
            int elemEnd = elemStart + e.length();
            // Mientras su posición esté entre los índices de inicio y fin...
            //e.debug();
            String text = "";
            // Ignoramos el elemEnd de los tags, sólo importa si empezaron dentro de la selección
            if (e.isTag()) {
                if (elemStart >= beginIndex && elemStart <= endIndex) {
                    text = e.getContent();
                } else {
                    continue;
                }

                // Hay que considerar que una frase puede estar dividida a lo largo de varios elementos
                // de texto, por lo tanto tenemos que primero, ignorar aquellos fragmentos de texto
                // que caigan completamente fuera de la selección.
                // Los fragmentos restantes son cortados mediante tres criterios:
                //  - Es inicio de frase
                //  - Es parte del medio de la frase
                //  - Es fin de frase
            } else {
                // El elemento está al lado derecho de la selección (no nos interesa, pero todavía pueden haber elementos que sí)
                if (beginIndex > elemEnd && endIndex > elemEnd) {
                    continue;

                    // El elemento está al lado izquierdo de la selección (todavía no hemos llegado)
                } else if (beginIndex < elemStart && endIndex < elemStart) {
                    continue;

                    // Parte del inicio del texto
                } else if (beginIndex <= elemStart && endIndex <= elemEnd) {
                    //System.out.println("INICIO");
                    offset = 0;
                    text = e.getContent().substring(
                            offset,
                            endIndex - elemStart
                    );

                    // Parte del medio del texto
                } else if (beginIndex >= elemStart && endIndex <= elemEnd) {
                    //System.out.println("MEDIO");
                    offset = beginIndex - elemStart;
                    text = e.getContent().substring(
                            offset,
                            offset + (endIndex - beginIndex)
                    );

                    // Parte del final del texto
                } else if (beginIndex >= elemStart && endIndex >= elemEnd) {
                    //System.out.println("FINAL");
                    offset = beginIndex - elemStart;
                    text = e.getContent().substring(
                            offset,
                            offset + (elemEnd - beginIndex));

                    // El elemento está completamente contenido en la selección
                } else {
                    text = e.getContent();
                }
            }
            //System.out.println("T: " + text);
            sb.append(text);
        }
        return sb.toString();
    }

    public String getAll(String substring) {
        if (substring != null && this.plain.contains(substring)) {
            int beginIndex = this.plain.indexOf(substring);
            int endIndex = beginIndex + substring.length();
            return getAll(beginIndex, endIndex);
        }
        //System.out.println("Substring: " + substring + "\nIs NOT contained in: \n" + this.plain + "\n****");
        return "";
    }

    // Obtenemos una lista de objetos Elem (texto o tags) que recuerden la posición que tendrían
    // si los buscáramos a partir del texto plano.
    public static List<Elem> parseElements(String html) {
        List<Elem> elements = new LinkedList<>();
        Pattern pattern = Pattern.compile("(<[^>]*>)"); // Captura grupos <*cualquier texto*>
        Matcher matcher = pattern.matcher(html);
        int lastTagEnd = 0;
        int tagOffset = 0;
        // Por cada tag <*> o </*> que encontremos
        while(matcher.find()) {
            String tag = matcher.group();
            int tagStart = matcher.start();
            int tagEnd = matcher.end();

            // Primero, Guardamos el hay texto (si hay) entre el final de este tag  y el comienzo del siguiente
            if (lastTagEnd < tagStart) {
                elements.add(new Elem(html.substring(lastTagEnd, tagStart), Elem.TYPE_TEXT, lastTagEnd - tagOffset));
            }

            // Agregamos un nuevo elemento.
            elements.add(new Elem(tag, Elem.TYPE_TAG, tagStart - tagOffset));

            tagOffset += (tagEnd - tagStart);
            lastTagEnd = tagEnd;
        }
        // Cogemos el texto restante hasta el final
        if (lastTagEnd < html.length()) {
            elements.add(new Elem(html.substring(lastTagEnd), Elem.TYPE_TEXT, lastTagEnd));
        }
        return elements;
    }

    public void debug() {
        for(Elem e : elements) {
            e.debug();
        }
    }


    public String toString() {
        return this.plain;
    }

}


class Elem {

    static final int TYPE_TEXT = 0;
    static final int TYPE_TAG = 1;

    final String content;
    final int type;
    final int startIndex;


    public Elem(String content, int type, int startIndex) {
        this.content = content;
        this.type = type;
        this.startIndex = startIndex;
    }

    String getContent() { return this.content; }
    String getType() { return this.type == TYPE_TEXT ? "TXT" : "TAG"; }
    int getStart() { return this.startIndex; }
    int getEnd() { return this.startIndex + content.length(); }
    int length() { return this.content.length(); }
    boolean isText() { return type == TYPE_TEXT; }
    boolean isTag() { return type == TYPE_TAG; }

    public void debug() {
        System.out.println("[" + getType() + "][" + getStart() + ", " + getEnd() + "] => " + getContent());
    }
    public String toString() { return content; }
}