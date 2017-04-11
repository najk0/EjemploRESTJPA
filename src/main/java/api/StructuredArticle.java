package api;

import data.Article;
import data.Section;
import net.htmlparser.jericho.*;

import java.util.ArrayList;
import java.util.List;

public class StructuredArticle {

    private final String title;

    private final String html;

    private final List<Section> sections;


    private final Source src;

    private List<SectionBlock> blocks;


    public StructuredArticle(String title, String html, List<Section> sections) {
        this.title = title;
        this.html = html;
        this.sections = sections;

        src = new Source(html);
        this.blocks = getSectionBlocks();
    }

    /* Trocea el HTML del artículo y devuelve tantos trozos de HTML como secciones hay */
    private List<SectionBlock> getSectionBlocks() {
        List<SectionBlock> sectionBlocks = new ArrayList<SectionBlock>();
        // Para determinar los bloques que comprende cada sección usamos los spans que
        // encabezan las mismas. Se identifican por pertenecer a la clase "mw-headline"
        // y tienen como atributo id el anchor de la sección
        List<Element> allSpans = src.getAllElements(HTMLElementName.SPAN);

        int currSpanIndex = 0; // El nº del span actual
        int leftIndexOf = -1; // Puntero situado en |<span class="mw-... (en concreto el span que contiene la sección anterior a la actual)
        int rightIndexOf = -1; // Como el anterior pero situado en el span de la sección actual

        // El criterio de búsqueda es encontrar los spans con anchors que coincidan con los
        // id de los span. Por ello recorremos las secciones.
        for(int currSectionIndex = 0; currSectionIndex < sections.size(); currSectionIndex++) {
            Section currSection = sections.get(currSectionIndex);
            String currSectionAnchor = sections.get(currSectionIndex).getAnchor();
            String nextSectionAnchor = null;
            if (currSectionIndex < sections.size() - 1) {
                nextSectionAnchor = sections.get(currSectionIndex + 1).getAnchor();
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
                            if (leftIndexOf == -1) {
                                sectionBlocks.add(new SectionBlock(html.substring(0, span.getBegin()), Section.HEADER));
                            } else {
                                sectionBlocks.add(new SectionBlock(html.substring(leftIndexOf, rightIndexOf), currSection));
                            }
                            // Continuamos con la próxima sección
                            break;
                        }
                    }
                }
            }
        }
        // Agregamos el último bloque que contiene la última sección
        // TODO asegurar que no se agregan secciones dos veces (si hay una sola seccion o un caso extraño)
        sectionBlocks.add(new SectionBlock(html.substring(leftIndexOf), sections.get(sections.size() - 1)));

        return sectionBlocks;
    }

    public Article asArticle() {
        Article a = new Article();
        a.setTitle(title);
        List<Section> articleSections = new ArrayList<Section>();
        for(SectionBlock sb : blocks) {
            articleSections.add(sb.asSection());
        }
        a.setSections(articleSections);

        return a;
    }

    // Devuelve el contenido del artículo en texto plano
    // de manera similar al query de la api
    public String extractAllText() {
        return src.getTextExtractor().toString();
    }

    // debug
    public void printAllTags() {
        List<Tag> tags = src.getAllTags();
        System.out.println("nº of tags: " + tags.size());
        for (Tag tag : tags) {
            System.out.println("-------------------------------------------------------------------------------");
            System.out.println(tag.getDebugInfo());
            System.out.println("Source text with content:\n"+tag);
        }
    }

    // debug
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



class SectionBlock {

    private final String html;

    private final Section section;


    private List<Paragraph> paragraphs;


    public SectionBlock(String sectionHTML, Section section) {
        this.html = sectionHTML;
        this.section = section;

        paragraphs = getParagraphs();
    }

    private List<Paragraph> getParagraphs() {
        List<Paragraph> paragraphs = new ArrayList<>();
        Source src = new Source(this.html);
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
    // Equivalente al texto plano que contiene la sección
    public String getAllPlainText() {
        StringBuilder sb = new StringBuilder();

        for(Paragraph p : paragraphs) {
            sb.append(p.getPlain()).append("\n\n");
            // en un futuro necesitaremos devolver el texto marcado en HTML para mostrarlo con formato en la página web
            //sb.append("<p>").append(p.getPlain()).append("</p>\n\n");
        }

        return sb.toString();
    }

    public Section asSection() {
        Section s = new Section();
        s.setName(section.getName());
        s.setAnchor(section.getAnchor());
        s.setNumber(section.getNumber());
        s.setIndex(section.getIndex());
        s.setDepth(section.getDepth());
        s.setContent(getAllPlainText());
        return s;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("SectionBlock for section ").append(section.getName()).append(". HTML: \n");
        sb.append(html).append("\n");
        sb.append("------------------------------\n");

        return sb.toString();
    }

}



class Paragraph {

    private final String html, plain;


    public Paragraph(Element p) {
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