package services;

import api.WikiAPI;
import data.Article;
import data.ArticleInfo;
import data.SearchResults;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

@Path("api")
public class WikiService {

    private WikiAPI api;

    public WikiService() {
        api = new WikiAPI();
    }

    @GET
    @Produces({"application/json"})
    @Path("{keywords}")
    public Response retrieve(@PathParam("keywords") String encodedKeywords) throws UnsupportedEncodingException {
        String decodedKeywords = "";
        decodedKeywords = URLDecoder.decode(encodedKeywords, "UTF-8");
        String keywords = decodedKeywords.replace(" ", "_");
        Article article;
        String title;
        // Si se provee un link directo al artículo, extraemos el título
        if (api.isValidLink(keywords)) {
            title = api.getTitleFromLink(keywords);
        } else {
            title = keywords;
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
                    + "\nEsta es toda la info que conocemos:\n" + info);
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        System.out.println("Las palabras de búsqueda \"" + keywords + "\""
                + " coinciden con el nombre del artículo");
        //String urlEncodedTitle = URLEncoder.encode(title, "UTF-8");
        article = api.getArticle(title);

        return  Response
                .status(Response.Status.OK)
                .entity(article)
                .build();
    }


    @GET
    @Produces({"application/json"})
    @Path("{keywords}/search")
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
    @Path("{keywords}/info")
    public Response getArticleInfo(@PathParam("keywords") String keywords) {
        ArticleInfo info = api.getArticleInfo(keywords);
        System.out.println("Las palabras de búsqueda \"" + keywords +
                "\" han devuelto la siguiente info:\n" + info);

        return  Response
                .status(Response.Status.OK)
                .entity(info)
                .build();
    }

}