package data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Article {

    @XmlAttribute
    private String title;


    private Sections sections;


    public Article() {
        super();
        title = "NULL";
        sections = new Sections();
    }

    public Article(String title, Sections sections) {
        this();
        this.title = title;
        this.sections = sections;
    }

    public String getTitle() {
        return title;
    }

    public Sections getSections() {
        return sections;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n******************************\n");
        sb.append("\"").append(title).append("\"\n");
        for(Section s : sections) {
            sb.append("\t").append(s);
        }
        return sb.toString();
    }

}
