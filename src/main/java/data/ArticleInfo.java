package data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ArticleInfo {

    @XmlAttribute
    private String articleName;

    @XmlAttribute
    private String redirectsTo;

    @XmlAttribute
    private Boolean isDisambiguation;


    // Constructor vacío necesario para que funcionen las anotaciones XML
    // también usado para indicar que el artículo no existe
    public ArticleInfo() {
        super();
    }

    // Constructor básico. El artículo existe pero no conocemos más información.
    // Asumimos que el artículo no es ni de desambiguación ni de redirección
    // no seteando los campos pertinentes. En los métodos resolvemos el caso
    // de que no estén seteados.
    public ArticleInfo(String articleName) {
        this();
        this.articleName = articleName;
    }

    // Constructor que indica que el artículo redirige a otro,
    // por lo que no puede tratarse también de un artículo de desambiguación
    public ArticleInfo(String articleName, String redirectsTo) {
        this(articleName);
        this.redirectsTo = redirectsTo;
        isDisambiguation = false;
    }

    // Constructor que indica que es un artículo de desambiguación,
    // por lo que no puede tratarse de una redirección
    public ArticleInfo(String articleName, boolean isDisambiguation) {
        this(articleName);
        this.isDisambiguation = isDisambiguation;
    }

    public String getArticleName() {
        return articleName;
    }

    // El título puede encontrarse en Wikipedia?
    public boolean exists() {
        return containsSomething(articleName);
    }

    // Devuelve el artículo al cual este redirige
    public String getRedirectsTo() {
        return redirectsTo;
    }

    // Es un artículo cuyo único proposito es redigir a otro?
    public boolean isRedirection() {
        return redirectsTo != null && redirectsTo.length() > 0;
    }

    // Es un artículo el cual proporciona enlaces a otros artículos de título muy similar?
    public boolean isDisambiguation() {
        if (isDisambiguation != null) {
            return isDisambiguation;
        }
        return false; // Asumimos que si no hemos indicado que el artículo es de desambiguación, no lo es
    }

    // Es un artículo normal y corriente si corresponde a un título
    // y no es una página de desambiguación o hace una redirección.
    public boolean isArticle() {
        return containsSomething(articleName) &&
                containsSomething(redirectsTo) == false &&
                (isDisambiguation == null || isDisambiguation == false);
    }

    private boolean containsSomething(String str) {
        return str != null && str.length() > 0;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ArticleInfo] {");
        sb.append("\n\texists: ").append(exists());
        sb.append("\n\tarticleName: ").append(getArticleName());
        sb.append("\n\tredirectsTo: ").append(getRedirectsTo());
        sb.append("\n\tisDisambiguation: ").append(isDisambiguation());
        sb.append("\n}");
        return sb.toString();
    }

}
