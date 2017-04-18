package api;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.body.MultipartBody;
import data.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiAPI {

    private String lang = "en";


    public WikiAPI() {

    }

    public Article getArticle(String title) {
        StructuredArticle sa = getStructuredArticle(title);
        return sa.asArticle();
    }

    @Deprecated
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

    public StructuredArticle getStructuredArticle(String title) {
        MultipartBody mb = getBaseBody();
        mb.field("action", "parse")
                .field("prop", "sections|text|displaytitle")
                .field("exsectionformat", "raw")
                .field("indexpageids", 1)
                .field("disablelimitreport", 1)
                .field("page", title);
        try {
            JSONObject json = mb.asJson().getBody().getObject();
            JSONObject parseObject = json.getJSONObject("parse");

            String trueArticleTitle = parseObject.getString("displaytitle");

            JSONObject textObject = parseObject.getJSONObject("text");
            String articleHTML = textObject.getString("*");


            // Preciosos los nombres
            JSONArray sectionsArray = parseObject.getJSONArray("sections");
            List<RawSection> noHierarchySections = getRawSections(sectionsArray);
            Sections sections = new Sections(noHierarchySections);

            return new StructuredArticle(trueArticleTitle, articleHTML, sections);

        } catch (UnirestException e) {
            e.printStackTrace();
        }

        return null;
    }

    public SearchResults getSearchResults(String keywords) {
        MultipartBody mb = getBaseBody();
        mb.field("action", "opensearch")
                .field("prop", "sections|text|displaytitle")
                .field("search", keywords)
                .field("namespace", 0)
                .field("limit", 10)
                .field("redirects", "return");
        try {
            JSONArray json = mb.asJson().getBody().getArray();

            // Convertimos los arrays en JSON a listas de String
            JSONArray tempJSONArray = json.getJSONArray(1);
            List<String> titlesList = new ArrayList<>();
            for(int i = 0; i < tempJSONArray.length(); i++) {
                String title = tempJSONArray.getString(i);
                titlesList.add(title);
            }

            tempJSONArray = json.getJSONArray(2);
            List<String> descriptionsList = new ArrayList<>();
            for(int i = 0; i < tempJSONArray.length(); i++) {
                String description = tempJSONArray.getString(i);
                descriptionsList.add(description);
            }

            tempJSONArray = json.getJSONArray(3);
            List<String> linksList = new ArrayList<>();
            for(int i = 0; i < tempJSONArray.length(); i++) {
                String link = tempJSONArray.getString(i);
                linksList.add(link);
            }

            // Construimos el objeto que encapsula los resultados de búsqueda
            // a partir de las tres listas de resultados ya saneadas
            SearchResults srs = new SearchResults(titlesList, descriptionsList, linksList);
            return srs;

        } catch (UnirestException e) {
            e.printStackTrace();
        }
        List<String> emptyStringList = new ArrayList<>();
        return new SearchResults(emptyStringList, emptyStringList, emptyStringList);
    }

    //action=query&format=json&prop=extracts&titles=Wicket_gate&utf8=1&exsectionformat=raw
    @Deprecated
    public String getArticleTextHTML(String title) {
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

    @Deprecated
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

    public ArrayList<RawSection> getRawSections(JSONArray sections) {
        ArrayList<RawSection> articleSections = new ArrayList<>();

        for(int i = 0; i < sections.length(); i++) {
            JSONObject jsonSection = sections.getJSONObject(i);
            RawSection articleSection = new RawSection(jsonSection);
            articleSections.add(articleSection);
        }
        return articleSections;
    }

    @Deprecated
    public ArrayList<Section> getSectionsFromParse(JSONObject json) {
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
