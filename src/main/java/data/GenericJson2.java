package data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GenericJson2 {
    private String title;
    private String text;

    public GenericJson2(String title, String text) {
        this.title = title;
        this.text = text;
    }

}
