package store;

import data.People;
import data.Person;

import javax.persistence.*;
import java.util.List;

public class PersonJPA implements PersonStorage {
    public final static Person NOT_FOUND = new Person("Not found", "", "");
    private EntityManager em = Persistence.createEntityManagerFactory("dataSource").createEntityManager();

    @Override
    public boolean create(Person person) {
        EntityTransaction tx = em.getTransaction();
        em.getTransaction().begin();
        em.persist(person);
        em.getTransaction().commit();
        return true;
    }

    @Override
    public Person retrieve(String nif) {
        TypedQuery<Person> query = em.createNamedQuery("Person.findByNif", Person.class);
        query.setParameter("nif", nif);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
//            return new Person("Not found", "", "");
            return NOT_FOUND;
        }
    }

    @Override
    public Person update(Person person) {
        Person found = em.find(Person.class, person.getNif());
        em.getTransaction().begin();
        found.update(person);
        em.getTransaction().commit();
        return found;
    }

    @Override
    public Person delete(String nif) {
        Person found = em.find(Person.class, nif);
        em.getTransaction().begin();
        em.remove(found);
        em.getTransaction().commit();
        return found;
    }

    public People retrieveAll() {
        TypedQuery<Person> query = em.createNamedQuery("Person.findAll", Person.class);
        List<Person> people = query.getResultList();
        return new People(people);
    }
}
