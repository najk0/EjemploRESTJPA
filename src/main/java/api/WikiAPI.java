package api;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.body.MultipartBody;
import data.Article;
import data.Section;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            List<Section> sections = getSections(json);
            String articleTextPlain = getArticleTextPlain(title);
            System.out.println("articletext: " + articleTextPlain);
            for(int i = 0; i < sections.size(); i++) {
                String currentSectionName = sections.get(i).getName();
                String nextSectionName = null;
                if ((i + 1) < sections.size()) {
                    nextSectionName = sections.get(i + 1).getName();
                }
                String sectionText = getSectionTextPlain(articleTextPlain, currentSectionName, nextSectionName);
                sections.get(i).setContent(sectionText);
            }
            a.setSections(sections);

        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return a;
    }

    private String getSectionTextPlain(String articleTextPlain, String sectionTitle, String nextSectionTitle) {
        String leftPattern = "(\\n)+\\b" + sectionTitle + "\\b(\\n)+";
        String rightPattern;
        if (nextSectionTitle == null) {
            rightPattern = "$"; // Fin del texto
        } else {
            rightPattern = "(\\n)+\\b" + nextSectionTitle + "\\b";
        }

        Pattern lp = Pattern.compile(leftPattern);
        Matcher lm = lp.matcher(articleTextPlain);
        if (lm.find()) {
            int leftIndex = lm.end(0);
            System.out.println("leftIndex " + leftIndex);

            Pattern rp = Pattern.compile(rightPattern);
            Matcher rm = rp.matcher(articleTextPlain);
            if (rm.find()) {
                int rightIndex = rm.start(0);
                rightIndex = Math.max(leftIndex, rightIndex);
                System.out.println("rightIndex " + rightIndex);

                String sectionText = articleTextPlain.substring(leftIndex, rightIndex);
                System.out.println("S: " + sectionTitle);
                System.out.println("T: " + sectionText);
                System.out.println();
                return sectionText;
            }
            System.out.println();
        }
        return null;
    }

    @Deprecated
    private String getSectionTextHTMLArticle(String articleTitle, int sectionIndex) {
        // parse magic goes here
        /*Document doc = Jsoup.parse(articleHTML); // Optimizar... no tiene sentido hacer un parse cada vez que se coge una sección
        Elements spans = doc.getElementsByTag("span");
        for(int i = 0; i < spans.size(); i++) {
            Element span = spans.get(i);
            String spanText = span.text(); // Es posible que sea mejor idea usar la id, aunque seria necesario convertir los espacios a barras bajas, como mínimo
            if (sectionName.equals(spanText)) {

            }
        }*/
        MultipartBody mb = getBaseBody();
        mb.field("action", "parse")
                .field("prop", "text")
                .field("page", articleTitle)
                .field("section", sectionIndex)
                .field("redirects", 1);

        try {
            JSONObject json = mb.asJson().getBody().getObject();

            JSONObject parseObject = json.getJSONObject("parse");
            JSONObject textObject = parseObject.getJSONObject("text");
            String sectionText = textObject.getString("*");

            return sectionText;

        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return null;
    }

    //action=query&format=json&prop=extracts&titles=Wicket_gate&utf8=1&exsectionformat=raw

    @Deprecated
    private String getArticleTextHTML(String title) {
        String html = null;

        MultipartBody mb = getBaseBody();
        mb.field("action", "query")
                .field("prop", "extracts")
                .field("exsectionformat", "raw")
                .field("indexpageids", 1)
                .field("titles", title);
        try {
            JSONObject json = mb.asJson().getBody().getObject();

            JSONObject queryObject = json.getJSONObject("query");
            JSONArray pagesArray = queryObject.getJSONArray("pageids");
            String pageID = pagesArray.getString(0);
            JSONObject arrayPages = queryObject.getJSONObject("pages");
            JSONObject arrayID = arrayPages.getJSONObject(pageID);

            html = arrayID.getString("extract");

        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return html;
    }

    public String getArticleTextPlain(String title) {
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
