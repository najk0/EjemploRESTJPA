package api;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.body.MultipartBody;
import data.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiAPI {

    private String lang = "en";


    public WikiAPI() {

    }


    // http://regexr.com/
    public boolean isValidLink(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        Pattern p = Pattern.compile("((http)(s)?:\\/\\/)?(en\\.)?(wikipedia.org\\/wiki\\/[\\w]+)");
        Matcher m = p.matcher(str);
        return m.matches();
    }


    public String getTitleFromLink(String link) {
        String baseUrl = "wikipedia.org/wiki/";
        int startIndex = link.indexOf(baseUrl);
        return link.substring(startIndex + baseUrl.length());
    }



    // Link al Sandbox:
    // https://en.wikipedia.org/wiki/Special:ApiSandbox#action=parse&format=json&page=Mass&prop=text%7Csections%7Cdisplaytitle&disablelimitreport=1
    public JSONObject getArticleJSON(String title) {
        System.out.println("articleJSONtitle:" + title);
        MultipartBody mb = getBaseBody();
        mb.field("action", "parse")
                .field("prop", "sections|text|displaytitle")
                .field("disablelimitreport", 1)
                .field("page", title);
        try {
            JSONObject json = mb.asJson().getBody().getObject();
            return json;

        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getRandomArticleName() {
        MultipartBody mb = getBaseBody();
        mb.field("action", "query")
                .field("generator", "random")
                .field("grnnamespace", 0) // namespace de artículos
                .field("grnlimit", 1) // 1 nombre cada vez
                .field("indexpageids", 1) // para poder recuperar los datos a partir del id de página
                .field("redirects", 1);
        try {
            JSONObject json = mb.asJson().getBody().getObject();

            String pageId = json.getJSONObject("query").getJSONArray("pageids").getString(0);
            String articleName = json.getJSONObject("query").getJSONObject("pages")
                    .getJSONObject(pageId).getString("title");
            return articleName;

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

    // Link al Sandbox:
    // https://en.wikipedia.org/wiki/Special:ApiSandbox#action=query&format=json&prop=categories%7Cpageprops&indexpageids=1&redirects=1&clcategories=Category%3ADisambiguation+pages%7CCategory%3AAll+disambiguation+pages%7CCategory%3AAll+article+disambiguation+pages&ppprop=disambiguation
    public ArticleInfo getArticleInfo(String urlEncodedKeywords) {
        MultipartBody mb = getBaseBody();
        mb.field("action", "query")
                .field("prop", "categories|pageprops")
                .field("indexpageids", 1)
                .field("redirects", 1)
                .field("clcategories",
                        "Category:Disambiguation pages" +
                                "|Category:All disambiguation pages" +
                                "|Category:All article disambiguation pages")
                .field("ppprop", "disambiguation")
                .field("titles", urlEncodedKeywords);
        try {
            JSONObject json = mb.asJson().getBody().getObject();

            JSONObject queryObject = json.getJSONObject("query");
            JSONArray pagesArray = queryObject.getJSONArray("pageids");
            String pageId = pagesArray.getString(0);

            // Si el artículo no existe, el pageid es el String "-1"
            if ("-1".equals(pageId)) {
                return new ArticleInfo();
            }

            // El nombre de artículo real puede no coincidir con el usado para encontrarlo
            // por ejemplo, "queen" da lugar al artículo "Queen". Esto no es una redirección
            // sino el criterio que sigue Wikipedia para nombrar los artículos.
            String articleNameNormalized;
            // Comprobamos si el artículo redirige a otro
            if (queryObject.has("redirects")) {
                JSONArray redirectsArray = queryObject.getJSONArray("redirects");
                JSONObject redirectsObject = redirectsArray.getJSONObject(0);
                articleNameNormalized = redirectsObject.getString("from");
                String redirectsTo = redirectsObject.getString("to");
                return new ArticleInfo(articleNameNormalized, redirectsTo);
            }

            // Nos adentramos en la jerarquía del JSON para buscar las categorías a las que pertenece
            JSONObject pagesObject = queryObject.getJSONObject("pages");
            JSONObject idObject = pagesObject.getJSONObject(pageId);
            articleNameNormalized = idObject.getString("title");
            // Dado el query, sólo obtenemos categorías de desambiguación, por lo que
            // si tiene alguna de ellas, se trata de un artículo de desambiguación
            if (idObject.has("categories")) {
                return new ArticleInfo(articleNameNormalized, true);
            }

            // Llegado a este punto, el artículo sólo puede ser uno corriente y moliente
            return new ArticleInfo(articleNameNormalized);

        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
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


    private String urlEncode(String unencodedString) {
        String urlEncodedString = null;
        try {
            urlEncodedString = URLEncoder.encode(unencodedString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //No pasa nunca porque la codificación es siempre la misma y es válida
        }
        return urlEncodedString;
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
