package snippets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.MultipartBody;
import org.json.JSONObject;

public class WikiAPI {

    private static final String USER_AGENT = "Mozilla/5.0";

    private String lang = "en";

    // https://www.mediawiki.org/wiki/API:Data_formats

    public WikiAPI() {
        //mapper();
    }


    public JSONObject getPageText(String title) {
        MultipartBody mb = getBaseBody();
        mb.field("action", "query")
                .field("prop", "extracts")
                .field("explaintext", 1)
                .field("exsectionformat", "plain")
                .field("titles", title);
        try {
            return mb.asJson().getBody().getObject();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
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


    public void goodshittest() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse = getBaseBody()
                .field("action", "query")
                .field("titles", "Main Page")
                .field("prop", "revisions")
                .field("rvprop", "content")
                .asJson();

        System.out.println(jsonResponse.getBody());
    }


    public static void main(String[] args) throws UnirestException {
        WikiAPI api = new WikiAPI();
        //api.goodshittest();
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
