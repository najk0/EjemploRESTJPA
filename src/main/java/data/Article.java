package data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Article {

    private String title;

    private String topText;

    private ArrayList<Section> sections;


    public Article() {
        super();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTopText() {
        return topText;
    }

    public void setTopText(String topText) {
        this.topText = topText;
    }

    public ArrayList<Section> getSections() {
        return sections;
    }

    public void setSections(ArrayList<Section> sections) {
        this.sections = sections;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Article\n");
        sb.append("Title: ").append(title).append("\n");
        sb.append("Sections: \n");
        for(Section s : sections) {
            sb.append(s);
        }
        return sb.toString();
    }

}