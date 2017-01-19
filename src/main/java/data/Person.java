package data;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@NamedQueries({
        @NamedQuery(name="Person.findAll", query = "SELECT p FROM Person p"),
        @NamedQuery(name = "Person.findByNif", query = "SELECT p FROM Person p WHERE p.nif = :nif")
})
public class Person {
    private String name;
    private String surname;
    @Id
    private String nif;
    @OneToOne(cascade = CascadeType.ALL)
    private PostalAddress postalAddress;

    public Person() {
        super();
    }

    public Person(String name, String surname, String nif, PostalAddress postalAddress) {
        this.name = name;
        this.surname = surname;
        this.nif = nif;
        this.postalAddress = postalAddress;
    }

    public Person(String name, String surname, String nif) {
        this.name = name;
        this.surname = surname;
        this.nif = nif;
    }

    public String getNif() {
        return nif;
    }

    public void update(Person person) {
        name = person.name;
        surname = person.surname;
        postalAddress = person.postalAddress;
    }
}
