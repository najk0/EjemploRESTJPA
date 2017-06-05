package api;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import data.Article;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Cache {

    private static final String PATH = "cache/";

    private static final String listingFilename = "listing.txt";

    private SuggestTree listing = new SuggestTree(MAX_SUGGESTIONS);

    private static final int MAX_SUGGESTIONS = 5;

    private static Cache instance;

    private static final int SAVE_COUNT = 10;
    private int nextSaveCount;


    private Cache() {
        nextSaveCount = SAVE_COUNT;
    }

    public static Cache getInstance() {
        if (instance == null) {
            synchronized (Cache.class) {
                if (instance == null) {
                    instance = new Cache();
                }
            }
        }
        return instance;
    }

    public List<String> autocomplete(String query) {
        List<String> suggestionList = new ArrayList<String>();
        SuggestTree.Node suggestionsNode = listing.getAutocompleteSuggestions(query);
        if (suggestionsNode != null) {
            SuggestTree.Entry[] suggestions = suggestionsNode.getList();
            for(SuggestTree.Entry entry : suggestions) {
                suggestionList.add(entry.getTerm());
            }
        }
        return suggestionList;
    }

    public boolean isCached(String articleTitle) {
        File articleJson = new File(PATH + articleTitle + ".json");
        return articleJson.exists();
    }

    public Article retrieve(String articleTitle) {
        File articleJson = new File(PATH + articleTitle + ".json");
        if (articleJson.exists()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Article article = mapper.readValue(articleJson, Article.class);
                return article;

            } catch (IOException e) {
                System.err.println("Error I/O genérico al manipular el JSON del artículo "
                        + articleTitle + " al tratar de cargarlo desde caché.");
                e.printStackTrace();
            }
        } else {
            System.err.println("Error al obtener el fichero del listado de artículos de la caché."
                    + " La ruta " + articleJson.getAbsolutePath() + " no es válida o no existe.");
        }
        return null;
    }

    public void store(Article article) {
        String articleTitle = article.getTitle();
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Convertimos el artículo en un string JSON y lo escribimos directamente a un archivo
            mapper.writerWithDefaultPrettyPrinter().writeValue(
                    new File(PATH + article.getTitle() + ".json"), article);

            // Guardamos el nombre del artículo en la lista de sugerencias
            listing.put(article.getTitle().toLowerCase(), 1);

            // Cada X artículos accedidos guardamos la lista de artículos accedidos
            // Idealmente esto se haría sólo al cerrar la aplicación...
            nextSaveCount--;
            if (nextSaveCount <= 0) {
                save();
                nextSaveCount = SAVE_COUNT;
            }

        } catch (JsonGenerationException e) {
            System.err.println("Error generando el objeto JSON del artículo " + articleTitle
                    +" al tratar de guardarlo en caché.");

        } catch (JsonMappingException e) {
            System.err.println("Error mappeando el objeto JSON del artículo " + articleTitle
                    +" al tratar de guardarlo en caché.");

        } catch (IOException e) {
            System.err.println("Error I/O genérico al manipular el JSON del artículo "
                    + articleTitle + " al tratar de guardarlo en caché.");
        }
    }

    public void load() {
        File listingFile = getListing();

        try {
            BufferedReader br = new BufferedReader(new FileReader(listingFile));
            for(String line; (line = br.readLine()) != null; ) {
                listing.put(line, 1);
            }
            br.close();

        } catch (IOException e) {
            System.out.println("Error I/O al cargar el listado de artículos en caché.");
        }
    }

    public void save() {
        File listingFile = getListing();
        SuggestTree.Iterator iter = listing.iterator();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(listingFile));
            while(iter.hasNext()) {
                bw.write(iter.next().getTerm() + "\r\n"); // TODO escribir el peso también
            }
            bw.close();

        } catch (IOException e) {
            System.out.println("Error I/O al guardar el listado de artículos en caché.");
        }

    }

    private File getListing() {
        File listingFile = new File(PATH + listingFilename);
        if (listingFile.exists() == false) {
            try {
                listingFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Error al obtener el fichero del listado de artículos de la caché."
                        + " La ruta " + listingFile.getAbsolutePath() + " no es válida o no existe.");
                return null;
            }
        }
        return listingFile;
    }

}
