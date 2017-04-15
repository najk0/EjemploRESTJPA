package data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class Article {

    @XmlAttribute
    private final String title;

    @XmlAttribute
    private final Sections sections;


    public Article(String title, Sections sections) {
        super();
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
