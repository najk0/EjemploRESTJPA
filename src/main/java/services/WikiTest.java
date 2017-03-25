package services;

import api.WikiAPI;
import data.Article;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("test")
public class WikiTest {

    private WikiAPI api;

    public WikiTest() {
        api = new WikiAPI();
    }

    @GET
    @Produces({"application/xml", "application/json"})
    @Path("{title}")
    public Response retrieve(@PathParam("title") String title) {
        Article article = api.getArticle(title);
        System.out.println(article);

        return  Response
                .status(Response.Status.OK)
                .entity(article)
                .build();
    }

}
