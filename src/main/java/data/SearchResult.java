package data;

import javax.xml.bind.annotation.*;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchResult {

    @XmlAttribute
    private final String title;

    @XmlAttribute
    private final String description;

    @XmlAttribute
    private final String link;


    public SearchResult() {
        title = "NULL";
        description = "NULL";
        link = "NULL";
    }


    public SearchResult(String title, String description, String link) {
        super();
        this.title = title;
        this.description = description;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

}
