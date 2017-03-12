package data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map.Entry;
import java.util.Set;

@XmlRootElement
public class Page {
    @XmlElement(name = "page")
    private Set<Entry<String, JsonElement>> content;

    public Page(JsonObject json) {
        this.content = json.entrySet();
    }

}
