package services;

import data.Page;
import snippets.WikiAPI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("wiki")
public class WikiService {

    private WikiAPI api;

    public WikiService() {
        api = new WikiAPI();
    }

    @GET
    @Produces({"application/xml", "application/json"})
    @Path("{title}")
    public Response retrieve(@PathParam("title") String title) {
        Page page = api.getPage(title);
        Page p = new Page("asd", "123");
        return Response
                .status(Response.Status.OK)
                .entity(page)
                .build();
    }

}
