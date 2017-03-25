package api;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.body.MultipartBody;
import data.Article;
import data.Section;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class WikiAPI {

    private String lang = "en";


    public WikiAPI() {

    }

    public Article getArticle(String title) {
        Article a = new Article();

        MultipartBody mb = getBaseBody();
        mb.field("action", "parse")
                .field("prop", "sections")
                .field("page", title)
                .field("redirects", 1);

        try {
            JSONObject json = mb.asJson().getBody().getObject();
            // Obtenemos el título del artículo a partir del resultado de la API
            // y no de el texto usado para encontrar el artículo ya que debido a
            // redirecciones y otras causas el título final del artículo podría variar
            JSONObject parse = json.getJSONObject("parse");
            String articleTitle = parse.getString("title");
            a.setTitle(articleTitle);

            a.setSections(getSections(json));

        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return a;
    }

    public String getArticleText(String title) {
        String text = null;

        MultipartBody mb = getBaseBody();
        mb.field("action", "query")
                .field("prop", "extracts")
                .field("explaintext", 1)
                .field("exsectionformat", "plain")
                .field("indexpageids", 1)
                .field("titles", title);
        try {
            JSONObject json = mb.asJson().getBody().getObject();

            JSONObject arrayQuery = json.getJSONObject("query");
            JSONArray pagesArray = arrayQuery.getJSONArray("pageids");
            String pageID = pagesArray.getString(0);
            JSONObject arrayPages = arrayQuery.getJSONObject("pages");
            JSONObject arrayID = arrayPages.getJSONObject(pageID);

            text = arrayID.getString("extract");

        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return text;
    }

    private ArrayList<Section> getSections(JSONObject json) {
        ArrayList<Section> articleSections = new ArrayList<Section>();

        JSONObject parse = json.getJSONObject("parse");
        JSONArray sections = parse.getJSONArray("sections");

        for(int i = 0; i < sections.length(); i++) {
            JSONObject section = sections.getJSONObject(i);
            Section articleSection = new Section();

            String name = section.getString("line");
            articleSection.setName(name);

            String index = section.getString("index");
            articleSection.setIndex(Integer.parseInt(index));

            String number = section.getString("number");
            articleSection.setNumber(number);

            String level = section.getString("level");
            articleSection.setDepth(Integer.parseInt(level) - 1);

            articleSections.add(articleSection);
        }
        return articleSections;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    private MultipartBody getBaseBody() {
        return  Unirest.post(getBaseURL())
                .header("accept", "application/json")
                .field("format", "json")
                .field("utf-8", "1");
    }

    private String getBaseURL() {
        return "https://" + this.lang + ".wikipedia.org/w/api.php";
    }


    public static void main(String[] args) throws UnirestException {
        WikiAPI api = new WikiAPI();
    }

}
