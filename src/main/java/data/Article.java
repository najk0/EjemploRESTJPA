package data;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Article {

    @XmlAttribute
    private String title;

    @XmlElement
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
        for (Section s : sections) {
            sb.append("\t").append(s);
        }
        return sb.toString();
    }


    public List<String> spliter(String text) {
        ArrayList<String> list = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        text = text.trim();
        boolean dot = false;
        boolean comma = false;
        boolean dot3x = false;
        int ndots = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);


            if (c == '.') {
                ndots++;
                if (ndots == 3) {
                    dot3x = true;
                } else {
                    dot3x = false;
                }
                int size = sb.length();
                if (size > 0 && sb.substring(size - 1, size).matches("[0-9]*")) {
                    comma = true;
                    ndots = 0;
                }
                sb.append(c);


            } else {
                if (ndots == 1 || ndots == 3) {
                    if (!sb.toString().trim().matches("\\.") && !sb.toString().trim().matches("")) {
                        list.add(sb.toString());
                    }
                    sb.setLength(0);
                    ndots = 0;
                }
                if (comma == true && String.valueOf(c).matches("[0-9]*")) {
                    comma = false;

                }

                if (c == '?') {
                    sb.append(c);
                    list.add(sb.toString());
                    sb.setLength(0);
                    continue;
                }
                if (c == '!') {
                    sb.append(c);
                    list.add(sb.toString());
                    sb.setLength(0);
                    continue;
                }
                sb.append(c);
            }

        }
        if (!sb.toString().trim().matches("\\.") && !sb.toString().trim().matches("")) {
            list.add(sb.toString());
        }

       return list;
    }
}



