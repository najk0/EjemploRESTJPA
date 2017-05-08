package data;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

// TODO borrar. es posible devolver como respuesta una lista de SearchResult, por lo que esta clase no es necesaria.
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchResults {

    @XmlElementWrapper
    @XmlElement(name="searchresult")
    private final List<SearchResult> results;


    public SearchResults() {
        super();
        results = new ArrayList<>();
    }


    public SearchResults(List<String> titles, List<String> descriptions, List<String> links) {
        this();
        // Aseguramos que los resultados de búsqueda contienen
        // el mismo número de entradas para todos los campos
        if (titles.size() == descriptions.size() && descriptions.size() == links.size()) {
            for(int i = 0; i < titles.size(); i++) {
                SearchResult sr = new SearchResult(titles.get(i), descriptions.get(i), links.get(i));
                this.results.add(sr);
            }
        } else {
            throw new RuntimeException("Search results don't have the same amount of parameters on " +
                    "its title, description, and link arrays.\n Cannot instantiate SearchResults.");
        }
    }

    public SearchResult getResults(int index) {
        return results.get(index);
    }

    public List<SearchResult> getResults() {
        return results;
    }

    public int getSize() {
        return results.size();
    }

}
