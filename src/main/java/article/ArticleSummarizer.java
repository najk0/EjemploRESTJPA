package article;

import data.Article;
import data.Content;
import data.Section;
import data.Sections;
import net.sf.classifier4J.summariser.SimpleSummariser;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArticleSummarizer {

    private static SimpleSummariser summarizer;

    private final Article article;

    public ArticleSummarizer(Article article) {
        summarizer = new SimpleSummariser();
        this.article = article;
    }

    public String getAllContent() {
        StringBuilder sb = new StringBuilder();
        for(Section section : article.getSections()) {
            sb.append(section.getContent());
        }
        return sb.toString();
    }

    // http://stackoverflow.com/a/21430792
    public List<String> splitInLines(String content) {
        List<String> lines = new ArrayList<>();
        String pattern = "[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)";
        Pattern re = Pattern.compile(pattern, Pattern.MULTILINE | Pattern.COMMENTS);
        Matcher reMatcher = re.matcher(content);
        while (reMatcher.find()) {
            String group = reMatcher.group();
            lines.add(group);
        }
        return lines;
    }

    // http://stackoverflow.com/a/2687929
    public List<String> splitInLines2(String content) {
        List<String> lines = new ArrayList<>();
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
        iterator.setText(content);
        int start = iterator.first();
        for (int end = iterator.next();
             end != BreakIterator.DONE;
             start = end, end = iterator.next()) {
            String line = content.substring(start,end);
            lines.add(line);
        }
        return lines;
    }

    public String joinLines(List<String> lines) {
        StringBuilder sb = new StringBuilder();
        for(String line : lines) {
            sb.append(line).append(" ");
        }
        return sb.toString();
    }


    // TODO mejorar este método ya que el nº final de líneas no coincide con el % indicado.
    public String summarize(String text, int numSentences) {
        if (text == null || text.isEmpty()) {
            System.out.println(numSentences);
            return summarizer.summarise(text, numSentences);
        } else {
            return summarizer.summarise(text, numSentences);
        }
    }


    public Article getSummarizedArticle(float reductionFactor) {
        if (reductionFactor < 0 || reductionFactor > 1) {
            throw new RuntimeException();
        }
        // Obtenemos la lista de líneas resumidas
        List<String> summaryLines = getSummarizedLines(reductionFactor);

        // Declaramos las variables que usamos para obtener el contenido de cada sección
        Sections sections = article.getSections();
        int sectionIndex = 0;
        Content sectionContent = null;
        // Declaramos las variables que usamos para almacenar el resumen de cada sección
        Section summarySection;
        StringBuilder contentSummary = null;

        // Buscamos recuperar el HTML de los líneas de texto que han sobrevivido al resumen.
        // Para ello comprobamos en orden si las líneas están contenidas en el texto original
        // de cada sección, y recuperamos el HTML correspondiente al texto plano de la línea resumida.
        int summaryLineIndex = 0;
        String summaryLinePlain;
        List<Section> summarySectionList = new ArrayList<>();

        do {
            summaryLinePlain = summaryLines.get(summaryLineIndex);
            // Inicializamos el contenido de la sección de resumen
            if (contentSummary == null) {
                sectionContent = sections.getByIndex(sectionIndex).getContent();
                contentSummary = new StringBuilder();
            }

            // Buscamos una coincidencia en la sección actual con la siguiente línea resumida
            // Si no hay coincidencia pasamos a la siguiente sección.
            // Lo que almacenamos como contenido de la sección a mostrar será el contenido
            // de la línea de resumen en HTML.
            String summaryLineHtml = sectionContent.getAll(summaryLinePlain);

            /*System.out.println();
            System.out.println("(" + summaryLineIndex + ")");
            System.out.println("P: " + summaryLinePlain);
            System.out.println("H: " + summaryLineHtml);
            System.out.println("C: " + sectionContent.getPlain().contains(summaryLinePlain));*/

            // Comprobamos si la línea resumida está contenida en la sección actual
            if (summaryLineHtml.isEmpty() == false) {
                contentSummary.append(summaryLineHtml).append(" ");
                summaryLineIndex++;

                // Si no pertenece, significa que hemos pasado a la próxima sección, por lo que debemos
                // almacenar el contenido que hemos ido guardando en la sección resumida
            } else {
                String sectionSummary = contentSummary.toString();
                if (sectionSummary.isEmpty() == false) {
                    summarySection = new Section(sections.getByIndex(sectionIndex).getRawSection());
                    summarySection.setDisplayedContent(sectionSummary);
                    summarySectionList.add(summarySection);
                }
                contentSummary = null;

                // Actualizamos el puntero de sección actual y reiniciamos la info de la sección de resumen
                sectionIndex++;
                //System.out.println("Section content:");
                //System.out.println(sectionContent);
                //sectionContent.debug();
            }

        } while(summaryLineIndex < summaryLines.size() && sectionIndex < sections.getSize());

        Sections summarizedSections = new Sections(summarySectionList);
        Article summarizedArticle = new Article(this.article.getTitle(), summarizedSections);
        return summarizedArticle;
    }

    private List<String> getSummarizedLines(float reductionFactor) {
        // Dividimos el contenido del artículo en líneas individuales para saber cuántas hay
        // y poder dividirlas en los varios resúmenes que se harán para obtener el resumen
        // compuesto de todos los resúmenes parciales
        String allContentPlain = getAllContent();
        List<String> allContentLines = splitInLines(allContentPlain);
        int originalLineCount = allContentLines.size();

        List<String> summarizedLines = new ArrayList<>();
        // Bloques de al menos 30 líneas. Si tiene más de 30 líneas (p.e. 600 líneas) se divide en
        // bloques de 600/10 -> 60 líneas. Como mucho siempre habrán 10 bloques.
        // A partir de 300 líneas los bloques pasarán a tener (totalLineCount / 10) líneas.
        final int MAX_BLOCK_COUNT = 10;
        final int MIN_LINE_COUNT = 30;

        // Líneas por bloque que va a resumirse.
        int linesPerChunk;
        if (originalLineCount < MIN_LINE_COUNT) {
            linesPerChunk = originalLineCount;
        } else {
            linesPerChunk = Math.max(MIN_LINE_COUNT, originalLineCount / MAX_BLOCK_COUNT);
        }
        // Líneas que queremos que tenga cada bloque después de ser recumido.
        // No tiene por qué coincidir con el número final, pero se acerca bastante
        int goalLines;
        if (linesPerChunk == originalLineCount) {
            goalLines = originalLineCount;
        } else {
            goalLines = Math.round(linesPerChunk * reductionFactor);
        }
        int lineIndex = 0;
        System.out.println("Original line count: " + originalLineCount);
        System.out.println("Lines per chunk: " + linesPerChunk);
        System.out.println("Goal lines: " + goalLines);
        while(lineIndex < originalLineCount) {
            List<String> chunkSummaryLines = summarizeChunk(allContentLines.subList(
                   lineIndex,
                   Math.min(lineIndex + linesPerChunk, originalLineCount)),
                   goalLines
            );
            summarizedLines.addAll(chunkSummaryLines);
            lineIndex += linesPerChunk;
        }
        System.out.println("Summary line count: " + originalLineCount + " => " + summarizedLines.size());
        //System.out.println("Summarized line count: " + summarizedLines.size());
        //System.out.println("Summarized article content: \n" + summarizedArticleContent);
        return summarizedLines;
    }

    private List<String> summarizeChunk(List<String> contentChunkLines, int goalLines) {
        // Resumimos parte del contenido a un % del original.
        String contentChunk = joinLines(contentChunkLines);
        //System.out.println("***CC: " + contentChunk);
        String chunkSummary = summarize(contentChunk, goalLines);
        // El resumen lo dividimos en líneas para comprobar la efectividad del algoritmo TODO quitar
        List<String> chunkSummaryLines = splitInLines(chunkSummary);
        //System.out.println("Chunk line count: " + contentChunkLines.size() + " => " + chunkSummaryLines.size());
        return chunkSummaryLines;
    }


    public void printList(List<String> list) {
        int i = 1;
        for(String elem : list) {
            System.out.println("(" + i++ + "/" + list.size() + ")\t" + elem);
        }
    }

    public Article getArticle() {
        return article;
    }

}
