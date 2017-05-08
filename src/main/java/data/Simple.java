package data;

import javax.xml.bind.annotation.*;

/*
 Esta clase nos ha permitido realizar pruebas con funcionalidades avanzadas con las cuales
 no estamos acostumbrados a trabajar. En lugar de tratar de hacer funcionar clases con una
 estructura mucho más compleja, esta clase nos sirve de ejemplo hasta que funciona.
 Entre otras cosas nos ha permitido comprobar que las anotaciones XML funcionan correctamente.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Simple {

    public String name;


    public Simple() {}

    public Simple(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
