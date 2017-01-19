package main;

import data.Person;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import store.*;

import javax.inject.Singleton;

public class MyApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(PersonJPA.class).to(PersonJPA.class).in(Singleton.class);
    }
}
