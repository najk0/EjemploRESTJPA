package data;

import javax.xml.bind.annotation.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Sections implements Iterable<Section> {

    @XmlElementWrapper
    @XmlElement(name="searchResult")
    private Map<String, Section> sectionMap;

    // Contiene los números (p.e. 1.2) de las secciones en el orden en el que aparecen
    // de manera que podemos recuperar la lista de secciones del mapa en tiempo O(n)
    @XmlElementWrapper
    @XmlElement(name="sectionNumber")
    private List<String> sortedSectionNumbers;


    public Sections() {
        super();
        sortedSectionNumbers = new ArrayList<>();
        sectionMap = new HashMap<>();
    }

    public Sections(List<Section> sections) {
        this();
        for (Section s : sections) {
            String sectionNumber = s.getNumber();
            sortedSectionNumbers.add(sectionNumber);
            sectionMap.put(sectionNumber, s);
        }
    }

    public Section getByIndex(int index) {
        String number = getNumberByIndex(index);
        return sectionMap.get(number);
    }

    public Section getByNumber(String number) {
        return sectionMap.get(number);
    }

    public int getSize() {
        return this.sectionMap.size();
    }

    public Map<String, Section> getSectionMap() {
        return sectionMap;
    }

    // Devuelve una lista de todas las secciones hijas dado su número
    public List<Section> getChildrenByNumber(String number) {
        List<String> childrenNumbers = getChildrenNumbers(number);
        List<Section> childrenSections = new ArrayList<>();
        for(String childNumber : childrenNumbers) {
            childrenSections.add(getByNumber(childNumber));
        }
        return childrenSections;
    }

    public List<Section> getChildrenByIndex(int index) {
        return getChildrenByNumber(getNumberByIndex(index));
    }

    // Método de conveniencia que devuelve el número de una sección dado su index
    public String getNumberByIndex(int index) {
        return sortedSectionNumbers.get(index);
    }

    // p.e dado el número 1.2 queremos obtener todos aquellos hijos del nivel inmediatamente inferior:
    // 1.2.1, 1.2.2, 1.2.3... pero no 1.2.1.1, 1.2.2.1... etc
    // Explicacion breve del patrón:
    // ^ - Inicio de la cadena
    // number + \\. - El número a buscar más un punto (.)
    // [0-9]+ - al menos una combinación de números
    // $ - Fin de la cadena
    private List<String> getChildrenNumbers(String number) {
        final String pattern = "^" + number + "\\.[0-9]+$";
        List<String> childrenNumbers = new ArrayList<>();

        Pattern p = Pattern.compile(pattern);
        for(String s : sortedSectionNumbers) {
            Matcher m = p.matcher(s);
            if (m.find()) {
                childrenNumbers.add(s);
            }
        }
        return childrenNumbers;
    }

    public Iterator<Section> iterator() {
        Iterator<Section> it = new Iterator<Section>() {

            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < getSize();
            }

            @Override
            public Section next() {
                return getByIndex(index++);
            }

        };
        return it;
    }

    private int[] getNumberAsArray(String number) {
        String[] allNumber = number.split("\\.");
        int[] numberArray;

        // Si sólo hay un número
        if (allNumber.length == 0) {
            numberArray = new int[]{Integer.parseInt(number)};

        } else {
            numberArray = new int[allNumber.length];
            for (int i = 0; i < allNumber.length; i++) {
                int numberAsInt = Integer.parseInt(allNumber[i]);
                numberArray[i] = numberAsInt;
            }
        }
        return numberArray;
    }

}
