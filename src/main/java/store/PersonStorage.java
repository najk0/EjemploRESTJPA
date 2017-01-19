package store;

import data.Person;
import data.People;

public interface PersonStorage {
    boolean create(Person person);
    Person retrieve(String nif);
    Person update(Person person);
    Person delete(String nif);
    People retrieveAll();
}
