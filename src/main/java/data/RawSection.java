package data;

import org.json.JSONObject;


public class RawSection {

    private final String level;

    private final String line;

    private final String number;

    private final String index;

    private final String fromtitle;

    private final String anchor;


    public RawSection(JSONObject jsonSection) {
        this.anchor = jsonSection.getString("anchor");
        this.line = jsonSection.getString("line");
        this.index = jsonSection.getString("index");
        this.number = jsonSection.getString("number");
        this.fromtitle = jsonSection.getString("fromtitle");
        this.level = jsonSection.getString("level");
    }

    public String getLevel() {
        return level;
    }

    public String getLine() {
        return line;
    }

    public String getNumber() {
        return number;
    }

    public String getIndex() {
        return index;
    }

    public String getFromtitle() {
        return fromtitle;
    }

    public String getAnchor() {
        return anchor;
    }

}
