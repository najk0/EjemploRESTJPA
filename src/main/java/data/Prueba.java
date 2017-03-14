package data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Prueba {
    private String name;
    private String surname;

    public Prueba() {
        super();
    }

    public Prueba(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

}
