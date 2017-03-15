package snippets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.body.MultipartBody;
import data.Page;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class WikiAPI {

    private static final String USER_AGENT = "Mozilla/5.0";

    private String lang = "en";


    public WikiAPI() {
        //mapper();
    }


    public Page getPage(String title) {
        MultipartBody mb = getBaseBody();
        mb.field("action", "query")
                .field("prop", "extracts")
                .field("explaintext", 1)
                .field("exsectionformat", "plain")
                .field("indexpageids", 1)
                .field("titles", title);
        try {
            JSONObject json = mb.asJson().getBody().getObject();
            for (String element : json.keySet()) {
                System.out.println(element);
            }
            JSONObject arrayQuery   = json.getJSONObject("query");
            JSONArray pagesArray    = arrayQuery.getJSONArray("pageids");
            String pageID           = pagesArray.getString(0);
            JSONObject arrayPages   = arrayQuery.getJSONObject("pages");
            JSONObject arrayID      = arrayPages.getJSONObject(pageID);

            String text = arrayID.getString("extract");
            System.out.println(text);
            Page page = new Page(title, text);
            return page;
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        System.err.println("oops! ---------------------------------------------");
        return null; // TODO
    }


    public void setLang(String lang) {
        this.lang = lang;
    }



    private MultipartBody getBaseBody() {
        MultipartBody mb = Unirest.post(getBaseURL())
                .header("accept", "application/json")
                .field("format", "json");
        return mb;
    }



    private String getBaseURL() {
        return "https://" + this.lang + ".wikipedia.org/w/api.php";
    }


    public static void main(String[] args) throws UnirestException {
        WikiAPI api = new WikiAPI();
    }

    // http://unirest.io/java.html (Serialization)
    // TODO mover a otro sitio
    private static void mapper() {
        // Only one time
        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
