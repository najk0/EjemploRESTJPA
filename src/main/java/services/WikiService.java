package services;

import data.Article;
import api.WikiAPI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("api")
public class WikiService {

    private WikiAPI api;

    public WikiService() {
        api = new WikiAPI();
    }

    @GET
    @Produces({"application/xml", "application/json"})
    @Path("{title}")
    public Response retrieve(@PathParam("title") String title) {
        Article article = api.getArticle(title);
        System.out.println("Requested article " + title);
        System.out.println(article);

        return  Response
                .status(Response.Status.OK)
                .entity(article)
                .build();
    }

}
