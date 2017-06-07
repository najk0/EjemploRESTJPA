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

    private final SuggestTree listing;

    private static final int MAX_SUGGESTIONS = 5;

    private static Cache instance;

    private static final int SAVE_COUNT = 10;
    private int nextSaveCount;

    private static final String NEWLINE = "\r\n";


    private Cache() {
        listing = new SuggestTree(MAX_SUGGESTIONS);
        loadListing();
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

    // Devuelve una lista con los posibles nombres de artículo dadas sus letras iniciales
    public List<String> autocomplete(String query) {
        List<String> suggestionList = new ArrayList<String>();
        SuggestTree.Node suggestionsNode = listing.getAutocompleteSuggestions(query.toLowerCase());
        if (suggestionsNode != null) {
            SuggestTree.Entry[] suggestions = suggestionsNode.getList();
            for(SuggestTree.Entry entry : suggestions) {
                suggestionList.add(entry.getTerm());
            }
        }
        return suggestionList;
    }

    public boolean isCached(String filename) {
        File articleJson = new File(PATH + filename + ".json");
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

    public void store(Article article, String filename) {
        String articleTitle = article.getTitle();
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Convertimos el artículo en un string JSON y lo escribimos directamente a un archivo
            mapper.writerWithDefaultPrettyPrinter().writeValue(
                    new File(PATH + filename + ".json"), article);

            // Guardamos el nombre del artículo en la lista de sugerencias
            listing.put(filename.toLowerCase(), 1);

            // Cada X artículos accedidos guardamos la lista de artículos accedidos
            // Idealmente esto se haría sólo al cerrar la aplicación...
            nextSaveCount--;
            if (nextSaveCount <= 0) {
                saveListing();
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

    public void loadListing() {
        File listingFile = getListingFile();

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

    public void saveListing() {
        File listingFile = getListingFile();
        SuggestTree.Iterator iter = listing.iterator();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(listingFile));
            while(iter.hasNext()) {
                bw.write(iter.next().getTerm() + NEWLINE);
            }
            bw.close();

        } catch (IOException e) {
            System.out.println("Error I/O al guardar el listado de artículos en caché.");
        }

    }

    private File getListingFile() {
        File listingFile = new File(PATH + listingFilename);
        if (listingFile.exists() == false) {
            try {
                listingFile.createNewFile();
                buildListingFile();

            } catch (IOException e) {
                System.err.println("Error al obtener el fichero del listado de artículos de la caché."
                        + " La ruta " + listingFile.getAbsolutePath() + " no es válida o no existe.");
                return null;
            }
        }
        return listingFile;
    }


    // Cargamos todos los nombres de los artículos en caché en el listing.
    // Este método se ejecuta si borramos listing.txt, para reconstruirlo.
    private void buildListingFile() {
        File listingFile = getListingFile();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(listingFile));
            for (File fileEntry : new File(PATH).listFiles()) {
                if (fileEntry.isFile() && fileEntry.getName().equals(listingFilename) == false) {
                    String articleAnchor = stripExtension(fileEntry.getName());
                    bw.write(articleAnchor + NEWLINE);
                }
            }
            bw.close();

        } catch(IOException e) {
            System.err.println("Error al generar el listado de artículos de la caché.");
        } catch(NullPointerException e) {
            // No hay ningún
        }
    }


    private String stripExtension(String filename) {
        // Handle null case specially.
        if (filename == null) return null;
        // Get position of last '.'.
        int pos = filename.lastIndexOf(".");
        // If there wasn't any '.' just return the string as is.
        if (pos == -1) return filename;
        // Otherwise return the string, up to the dot.
        return filename.substring(0, pos);
    }

}
