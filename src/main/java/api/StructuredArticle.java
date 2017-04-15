package api;

import data.Article;
import data.Content;
import data.Section;
import data.Sections;
import net.htmlparser.jericho.*;

import java.util.ArrayList;
import java.util.List;

public class StructuredArticle {

    private final String title;

    private final String html;

    private final Sections sections;


    private final Source src;


    public StructuredArticle(String title, String html, Sections sections) {
        this.title = title;
        this.html = html;
        this.sections = sections;

        src = new Source(html);
        setSectionsContent();
    }

    /* Trocea el HTML del artículo y devuelve tantos trozos de HTML como secciones hay */
    private void setSectionsContent() {
        // Para determinar los bloques que comprende cada sección usamos los spans que
        // encabezan las mismas. Se identifican por pertenecer a la clase "mw-headline"
        // y tienen como atributo id el anchor de la sección
        List<Element> allSpans = src.getAllElements(HTMLElementName.SPAN);

        int currSpanIndex = 0; // El nº del span actual
        int leftIndexOf = -1; // Puntero situado en |<span class="mw-... (en concreto el span que contiene la sección anterior a la actual)
        int rightIndexOf = -1; // Como el anterior pero situado en el span de la sección actual

        // El criterio de búsqueda es encontrar los spans con anchors que coincidan con los
        // id de los span. Por ello recorremos las secciones.
        for(int currSectionIndex = 1; currSectionIndex < sections.getSize(); currSectionIndex++) {
            //System.out.println("getSize: " + sections.getSize());
            Section currSection = sections.getByIndex(currSectionIndex);
            //System.out.println("Current section: " + currSectionIndex);
            String currSectionAnchor = currSection.getAnchor();
            String nextSectionAnchor = null;

            if (currSectionIndex < sections.getSize() - 1) {
                nextSectionAnchor = sections.getByIndex(currSectionIndex + 1).getAnchor();
            }

            // Para cada sección buscamos entre los spans restantes hasta que encontremos el que
            // tiene como id el anchor de la sección actual
            for(/* Se inicializa desde fuera */; currSpanIndex < allSpans.size(); currSpanIndex++) {
                Element span = allSpans.get(currSpanIndex);
                Attributes spanAttributes = span.getAttributes();

                // Los spans de sección pertenecen a la clase "mw-headline"
                Attribute classAttr = span.getAttributes().get("class");
                if (classAttr != null) {
                    String spanClass = classAttr.getValue();
                    // Comprobamos si el span es de sección
                    if ("mw-headline".equals(spanClass)) {
                        // Comprobamos que la id coincide con el de la sección actual
                        // si el orden de las secciones está asegurado esta comprobación
                        // no es necesaria pero como no lo sabemos la hacemos
                        String spanId = spanAttributes.getValue("id");
                        if (currSectionAnchor.equals(spanId)) {
                            // Indicamos de qué punto a qué punto cogemos parte del HTML del artículo
                            // (desde el principio del span que encabeza la sección hasta justo antes
                            // del span que encabeza la siguiente sección)
                            leftIndexOf = rightIndexOf;
                            rightIndexOf = span.getBegin();

                            // Normalmente esperaríamos a la segunda sección para delimitar secciones
                            // porque es cuando conocemos la posición de inicio de una sección y
                            // la siguiente. Sin embargo también debemos almacenar la cabecera,
                            // que es la sección inicial del documento, y que no aparece en la
                            // lista de secciones.
                            Content content;
                            if (leftIndexOf == -1) {
                                content = new Content(html.substring(0, span.getBegin()));
                            } else {
                                // En el caso más general, tomamos el bloque HTML que va desde la etiqueta
                                // que encabeza la sección previa hasta la etiqueta de la sección actual
                                // Pasamos como parámetro la sección anterior porque tenemos el trozo de
                                // HTML de la sección anterior
                                content = new Content(html.substring(leftIndexOf, rightIndexOf));
                            }
                            // El trozo de HTML seleccionado es el contenido de la sección actual
                            currSection.setContent(content);
                            // Continuamos con la próxima sección
                            break;
                        }
                    }
                }
            }
        }
        // Agregamos el último bloque que contiene la última sección
        // TODO asegurar que no se agregan secciones dos veces (si hay una sola seccion o un caso extraño)
        sections.getByIndex(sections.getSize() - 1).setContent(new Content(html.substring(leftIndexOf)));
    }

    public Article asArticle() {
        return new Article(title, sections);
    }

    // Devuelve el contenido del artículo en texto plano
    // de manera similar al query de la api
    public String extractAllText() {
        return src.getTextExtractor().toString();
    }

    /** debug */
    public void printAllTags() {
        List<Tag> tags = src.getAllTags();
        System.out.println("nº of tags: " + tags.size());
        for (Tag tag : tags) {
            System.out.println("-------------------------------------------------------------------------------");
            System.out.println(tag.getDebugInfo());
            System.out.println("Source text with content:\n"+tag);
        }
    }

    /** debug */
    public void printAllElements() {
        List<Element> elementList = src.getAllElements();
        System.out.println("nº of elements: " + elementList.size());
        for (Element element : elementList) {
            System.out.println("-------------------------------------------------------------------------------");
            System.out.println(element.getDebugInfo());
            if (element.getAttributes()!=null)
                System.out.println("XHTML StartTag:\n"+element.getStartTag().tidy(true));
            System.out.println("Source text with content:\n"+element);
        }
    }

    private static void print(Object o) {
        System.out.println(o);
    }

}
