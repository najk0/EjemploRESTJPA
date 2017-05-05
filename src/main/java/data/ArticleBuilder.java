package data;

import net.htmlparser.jericho.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ArticleBuilder {

    private final String title;

    private final String html;

    private final Source src;

    private final List<RawSection> sections;


    public ArticleBuilder(JSONObject json) {
        this.title = json.getJSONObject("parse").getString("displaytitle");
        this.html = json.getJSONObject("parse").getJSONObject("text")
                .getString("*");
        this.src = new Source(this.html);

        JSONArray sectionsArray = json.getJSONObject("parse").getJSONArray("sections");
        ArrayList<RawSection> articleSections = new ArrayList<>();
        articleSections.add(RawSection.HEADER);
        for(int i = 0; i < sectionsArray.length(); i++) {
            JSONObject jsonSection = sectionsArray.getJSONObject(i);
            RawSection articleSection = new RawSection(jsonSection);
            articleSections.add(articleSection);
        }
        this.sections = articleSections;
    }


    public String getSectionHTML(int index) {
        String sectionHTML;
        int lastIndex = sections.size() - 1;
        // Primera sección
        if (index == 0) {
            // Si queremos obtener la introducción cogemos desde el principio hasta la primera sección
            Element firstSpan = getSpan(sections.get(1).getAnchor()); // 1, porque la primera sección es la Introducción y su anchor no corresponde al HTML del artículo
            sectionHTML = html.substring(0, firstSpan.getBegin());

        // Última sección
        } else if (index == lastIndex) {
            Element lastSpan = getSpan(sections.get(lastIndex).getAnchor());
            sectionHTML = html.substring(lastSpan.getBegin());

        } else {
            Element currSpan = getSpan(sections.get(index).getAnchor());
            Element nextSpan = getSpan(sections.get(index + 1).getAnchor());
            sectionHTML = html.substring(currSpan.getBegin(), nextSpan.getBegin());
        }
        return sectionHTML;
    }


    public String getSectionHTML(String anchor) {
        String nextAnchor = getNextAnchor(anchor);
        Element sectionSpan = getSpan(anchor);
        Element nextSectionSpan = getSpan(nextAnchor);

        String sectionHTML;
        int startIndex = sectionSpan.getBegin();
        if (nextSectionSpan != null) {
            sectionHTML = html.substring(startIndex, nextSectionSpan.getBegin());
        } else {
            sectionHTML = html.substring(startIndex);
        }
        return sectionHTML;
    }


    private Element getSpan(String anchor) {
        List<Element> allSpans = src.getAllElements(HTMLElementName.SPAN);
        for (Element span : allSpans) {
            Attributes spanAttributes = span.getAttributes();
            Attribute spanClass = spanAttributes.get("class");
            if (spanClass != null) {
                boolean isSectionHeader = "mw-headline".equals(spanClass.getValue());
                if (isSectionHeader) {
                    String spanId = spanAttributes.getValue("id");
                    if (spanId != null && spanId.equals(anchor)) {
                        return span;
                    }
                }
            }
        }
        return null;
    }

    private String getNextAnchor(String anchor) {
        boolean getNext = false;
        for(RawSection rs : sections) {
            if (getNext) {
                return rs.getAnchor();
            }
            if (rs.getAnchor().equals(anchor)) {
                getNext = true;
            }
        }
        return null;
    }


    public String getTitle() {
        return title;
    }

    public int getSize() {
        return sections.size();
    }


    public String getHTML() {
        return html;
    }

}
