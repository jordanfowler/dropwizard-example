package com.dwbook.phonebook;

import com.dwbook.phonebook.health.NewContactHealthCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import com.dwbook.phonebook.resources.ContactResource;
import org.skife.jdbi.v2.DBI;
import io.dropwizard.jdbi.DBIFactory;
import com.sun.jersey.api.client.Client;
import io.dropwizard.client.JerseyClientBuilder;
import com.dwbook.phonebook.resources.ClientResource;
import com.google.common.cache.CacheBuilderSpec;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import io.dropwizard.auth.CachingAuthenticator;
import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.auth.basic.BasicCredentials;

public class App extends Application<PhonebookConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void initialize(Bootstrap<PhonebookConfiguration> b) {
    }

    @Override
    public void run(PhonebookConfiguration c, Environment e) throws Exception {
        LOGGER.info("Method App#run() called");
        for (int i = 0; i < c.getMessageRepetitions(); i++) {
            System.out.println(c.getMessage());
        }
        System.out.println("Additional message: " + c.getAdditionalMessage());

        
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(e, c.getDataSourceFactory(), "mysql");
        CachingAuthenticator<BasicCredentials, Boolean> authenticator = new CachingAuthenticator<BasicCredentials, Boolean>(e.metrics(), new PhonebookAuthenticator(jdbi), CacheBuilderSpec.parse("maximumSize=10000, expireAfterAccess=10m"));
        e.jersey().register(new BasicAuthProvider<Boolean>(authenticator, "Web Service Realm"));
        e.jersey().register(new ContactResource(jdbi, e.getValidator()));

        final Client client = new JerseyClientBuilder(e).build("REST Client");
        client.addFilter(new HTTPBasicAuthFilter("jordan", "testing123"));
        e.jersey().register(new ClientResource(client));

        e.healthChecks().register("New Contact Health Check", new NewContactHealthCheck(client));
    }
}