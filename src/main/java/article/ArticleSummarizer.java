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

    private List<String> getSummarizedLines(float originalReductionFactor) {
        // Dividimos el contenido del artículo en líneas individuales para saber cuántas hay
        // y poder dividirlas en los varios resúmenes que se harán para obtener el resumen
        // compuesto de todos los resúmenes parciales
        String allContentPlain = getAllContent();
        List<String> allContentLines = splitInLines(allContentPlain);
        int originalLineCount = allContentLines.size();

        List<String> summarizedLines = new ArrayList<>();

        // Un artículo nunca será resumido a menos de 10 líneas.
        final int MIN_SUMMARY_LINE_COUNT = 10;
        if (originalLineCount <= MIN_SUMMARY_LINE_COUNT) {
            return allContentLines;
        }

        // Bloques de 30 líneas o menos. Si tiene más de 30 líneas (p.e. 310 líneas) se divide en
        // bloques de 310/10 -> 31 líneas.
        // Como mucho habrá MAX_BLOCK_COUNT bloques enteros más uno para el resto de líneas.
        // A partir de (MIN_BLOCK_LINE_COUNT * MAX_BLOCK_COUNT) líneas los bloques pasarán a tener
        // (originalLineCount / MAX_BLOCK_COUNT) líneas.
        final int MAX_BLOCK_COUNT = 10;
        final int MIN_BLOCK_LINE_COUNT = 30;
        // Líneas por bloque que va a resumirse.
        int linesPerChunk;
        if (originalLineCount < MIN_BLOCK_LINE_COUNT) {
            linesPerChunk = originalLineCount;
        } else {
            linesPerChunk = Math.max(MIN_BLOCK_LINE_COUNT, originalLineCount / MAX_BLOCK_COUNT);
        }

        // Adaptamos el % de líneas a conservar entre un máximo y mínimo dependiendo
        // del número total de líneas del artículo original.
        // Si originalLineCount == MIN_SUMMARY_LINE_COUNT el factor de reducción es 1.
        // Tal y como incrementa originalLineCount el factor de reducción
        // disminuye como mucho al valor original.
        float reductionFactor = Math.max(originalReductionFactor,
                1f * MIN_SUMMARY_LINE_COUNT / originalLineCount);

        // Líneas que queremos que tenga cada bloque después de ser resumido.
        // Normalmente no coincide con el número final, pero se acerca bastante
        int goalLinesPerChunk = Math.round(linesPerChunk * reductionFactor);
        int lineIndex = 0;
        System.out.println("Número de líneas del artículo original: " + originalLineCount);
        System.out.println("---");
        System.out.println("Líneas por bloque de resumen: " + linesPerChunk);
        System.out.println("Líneas objetivo a resumir por bloque: " + goalLinesPerChunk);
        System.out.println("Factor de reducción %: " + reductionFactor);
        System.out.println("---");
        int blockCount = 0;
        while(lineIndex < originalLineCount) {
            List<String> chunkSummaryLines = summarizeChunk(allContentLines.subList(
                   lineIndex,
                   Math.min(lineIndex + linesPerChunk, originalLineCount)),
                   goalLinesPerChunk
            );
            summarizedLines.addAll(chunkSummaryLines);
            lineIndex += linesPerChunk;
            blockCount++;
        }
        System.out.println("Número de bloques: " + blockCount);
        System.out.println("Número de líneas inicial y final: " + originalLineCount + " => " + summarizedLines.size());
        System.out.println("---------------");
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
