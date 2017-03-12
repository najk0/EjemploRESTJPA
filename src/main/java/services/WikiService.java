package services;

import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import data.GenericJson;
import data.Page;
import data.Person;
import org.json.JSONObject;
import snippets.WikiAPI;
import store.PersonJPA;

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
        JSONObject jsonResponse = api.getPageText(title);
        GenericJson json = new GenericJson(jsonResponse);
        System.out.println("jsonResponse");
        System.out.println(jsonResponse.toString());
        return Response
                .status(Response.Status.OK)
                .entity(json)
                .build();
    }

}
