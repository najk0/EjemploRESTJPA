package data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Page {
    private String title;
    private String text;

    public Page() { super(); }

    public Page(String title, String text) {
        this.title  = title;
        this.text   = text;
    }

}
