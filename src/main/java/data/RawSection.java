package data;

import org.json.JSONObject;


public class RawSection {

    /* Definición de la sección inicial o cabecera, antes de todas
    las demás secciones, inicializada por defecto con esta información. */
    public static final RawSection HEADER =
            new RawSection("2", "Introduction", "0", "0", "Introduction");

    private final String level;

    private final String line;

    private final String number;

    private final String index;

    private final String anchor;


    public RawSection(JSONObject jsonSection) {
        this.level = jsonSection.getString("level");
        this.line = jsonSection.getString("line");
        this.number = jsonSection.getString("number");
        this.index = jsonSection.getString("index");
        this.anchor = jsonSection.getString("anchor");
    }

    private RawSection(String level, String line, String number, String index, String anchor) {
        this.level = level;
        this.line = line;
        this.number = number;
        this.index = index;
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

    public String getAnchor() {
        return anchor;
    }

}
