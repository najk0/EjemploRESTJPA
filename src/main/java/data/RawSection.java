package data;

import org.json.JSONObject;


public class RawSection {

    /* Definición de la sección inicial o cabecera, antes de todas
    las demás secciones, inicializada por defecto con esta información. */
    public static final RawSection HEADER =
            new RawSection("2", "Introduction", "0", "0",
                    "Unknown", "Introduction");

    private final String level;

    private final String line;

    private final String number;

    private final String index;

    private final String fromtitle;

    private final String anchor;


    public RawSection(JSONObject jsonSection) {
        this.level = jsonSection.getString("level");
        this.line = jsonSection.getString("line");
        this.number = jsonSection.getString("number");
        this.index = jsonSection.getString("index");
        this.fromtitle = jsonSection.getString("fromtitle");
        this.anchor = jsonSection.getString("anchor");
    }

    private RawSection(String level, String line, String number, String index, String fromtitle, String anchor) {
        this.level = level;
        this.line = line;
        this.number = number;
        this.index = index;
        this.fromtitle = fromtitle;
        this.anchor = anchor;
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
