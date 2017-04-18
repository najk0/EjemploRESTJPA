package services;

import com.sun.org.apache.xpath.internal.SourceTree;
import data.Article;
import api.WikiAPI;
import data.SearchResult;
import data.SearchResults;
import data.Simple;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("api")
public class WikiService {

    private WikiAPI api;

    public WikiService() {
        api = new WikiAPI();
    }

    @GET
    @Produces({"application/xml"})
    @Path("{title}")
    public Response retrieve(@PathParam("title") String title) {
        Article article = api.getArticle(title);
        System.out.println("Requested article: " + title);
        System.out.println(article);

        return  Response
                .status(Response.Status.OK)
                .entity(article)
                .build();
    }

    @GET
    @Produces({"application/xml"})
    @Path("{title}/sections")
    public Response retrieveSections(@PathParam("title") String title) {
        Article article = api.getArticle(title);
        System.out.println("Article sections for: " + title);

        return Response
                .status(Response.Status.OK)
                .entity(article.getSections())
                .build();
    }


    @GET
    @Produces({"application/xml"})
    @Path("{keywords}/search")
    public Response search(@PathParam("keywords") String keywords) {
        SearchResults results = api.getSearchResults(keywords);
        System.out.println("Searched for: " + keywords);

        return  Response
                .status(Response.Status.OK)
                .entity(results)
                .build();
    }

    /*
    @GET
    @Produces({"application/xml"})
    @Path("simple")
    public Response simple(String keywords) {
        System.out.println("Testing simple response");

        return  Response
                .status(Response.Status.OK)
                .entity(new Simple("ASD"))

                .build();
    }*/

}