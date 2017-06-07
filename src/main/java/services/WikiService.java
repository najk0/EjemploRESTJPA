package services;

import api.Cache;
import api.WikiAPI;
import article.ArticleParser;
import article.ArticleSummarizer;
import data.Article;
import article.ArticleSplitter;
import data.ArticleInfo;
import data.SearchResults;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

@Path("")
public class WikiService {

    private WikiAPI api;

    private Cache cache;

    public WikiService() {
        api = new WikiAPI();
        cache = Cache.getInstance();
    }

    @GET
    @Produces({"application/json"})
    @Path("wiki/{keywords}")
    public Response retrieve(@PathParam("keywords") String encodedKeywords) throws UnsupportedEncodingException {
        String decodedKeywords = URLDecoder.decode(encodedKeywords, "UTF-8");
        String keywords = decodedKeywords.replace(" ", "_");
        String titleAnchor;

        // Si se provee un link directo al artículo, extraemos el título
        if (api.isValidLink(keywords)) {
            titleAnchor = api.getTitleAnchorFromLink(keywords);
        } else {
            titleAnchor = keywords;
        }
        ArticleInfo info = api.getArticleInfo(keywords);
        if (info.isArticle()) {
            // saltamos hasta el final

        } else if (info.isRedirection()) {
            System.out.println("Las palabras de búsqueda \"" + keywords + "\" dan lugar a"
                    + " una redirección. Redirigiendo al artículo \"" + info.getRedirectsTo() + "\"...");
            return retrieve(info.getRedirectsTo());

        } else if (info.isDisambiguation()) {
            System.out.println("Las palabras de búsqueda \"" + keywords + "\" dan lugar a"
            + " un artículo de desambiguación. Se realizará una búsqueda.");
            return search(keywords);

        } else if (info.exists() == false) {
            System.out.println("No se ha podido encontrar un artículo que corresponda"
            + " con las palabras de búsqueda \"" + keywords + "\".");
            return search(keywords);

        } else {
            System.out.println("Error dadas las palabras de búsqueda: " + keywords + "."
                    + "\nNo se ha podido encontrar un artículo que corresponda."
                    + "\nEsto es todo lo que sabemos:\n" + info);
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        System.out.println("Las palabras de búsqueda \"" + keywords + "\""
                + " coinciden con el nombre del artículo");

        Article article;
        if (cache.isCached(decodedKeywords)) {
            article = cache.retrieve(decodedKeywords);
            System.out.println("Devuelto artículo desde caché.");

        } else {
            // Obtenemos el título, secciones y contenido del artículo de la API de Wikipedia
            JSONObject sourceJSON = api.getArticleJSON(titleAnchor);
            // A partir de esta información troceamos el contenido en secciones.
            ArticleSplitter ab = new ArticleSplitter(sourceJSON);
            Article splitArticle = ab.getSplitArticle();
            // El contenido todavía tiene etiquetas HTML no deseadas (b, i, etc...) - las eliminamos
            ArticleParser parser = new ArticleParser(splitArticle);
            Article parsedArticle = parser.getParsedArticle();
            // Resumimos el artículo a un % de su total
            ArticleSummarizer summarizer = new ArticleSummarizer(parsedArticle);
            System.out.println("Resumiendo artículo: " + parsedArticle.getTitle());
            article = summarizer.getSummarizedArticle(0.2f);
            // Lo almacenamos en caché
            cache.store(article, article.getTitle());
        }

        // Devolvemos el artículo en forma de respuesta HTTP
        Response response = Response
                .status(Response.Status.OK)
                .entity(article)
                .build();
        return response;
    }


    @GET
    @Produces({"application/json"})
    @Path("search/{keywords}")
    public Response search(@PathParam("keywords") String keywords) {
        SearchResults results = api.getSearchResults(keywords);
        System.out.println("Realizando un búsqueda de \"" + keywords + "\".");

        return  Response
                .status(Response.Status.OK)
                .entity(results)
                .build();
    }


    @GET
    @Produces({"application/json"})
    @Path("info/{keywords}")
    public Response getArticleInfo(@PathParam("keywords") String keywords) {
        ArticleInfo info = api.getArticleInfo(keywords);
        System.out.println("Las palabras de búsqueda \"" + keywords +
                "\" han devuelto la siguiente info:\n" + info);

        return  Response
                .status(Response.Status.OK)
                .entity(info)
                .build();
    }

    @GET
    @Produces({"application/json"})
    @Path("auto/{keywords}")
    public Response getSuggestedArticles(@PathParam("keywords") String keywords) {
        List<String> suggestions = cache.autocomplete(keywords);

        return  Response
                .status(Response.Status.OK)
                .entity(suggestions)
                .build();
    }

}