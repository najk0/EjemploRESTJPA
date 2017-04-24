package main;

import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;
import org.glassfish.jersey.server.ResourceConfig;
import providers.CORSFilter;
import providers.ObjectMapperProvider;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/rest")
public class MyApplication extends ResourceConfig {
    public MyApplication() {
        packages("services");
        register(JacksonFeatures.class);
        register(new MyApplicationBinder());
        register(new ObjectMapperProvider());
        register(new CORSFilter());
    }
}
