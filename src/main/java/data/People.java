package data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.HashSet;

@XmlRootElement
public class People {
    @XmlElement(name = "person")
    private Collection<Person> people;

    public People() {
        super();
        people = new HashSet<>();
    }

    public People(Collection<Person> people) {
        this.people = people;
    }
}
