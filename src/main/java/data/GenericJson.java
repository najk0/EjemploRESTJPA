package data;

import org.json.JSONObject;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GenericJson {
    @XmlElement(name = "content")
    private String content;


    public GenericJson(JSONObject json) {
        this.content = json.toString();
    }



}
