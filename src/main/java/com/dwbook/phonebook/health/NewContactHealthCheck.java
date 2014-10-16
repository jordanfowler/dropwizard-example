package com.dwbook.phonebook.health;

import com.codahale.metrics.health.HealthCheck;
import com.dwbook.phonebook.representations.Contact;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import javax.ws.rs.core.MediaType;

public class NewContactHealthCheck extends HealthCheck {
    private final Client client;

    public NewContactHealthCheck(Client c) {
        super();
        client = c;
    } 
    
    @Override
    protected Result check() throws Exception {
        WebResource contactResource = client.resource("http://localhost:8080/contact");
        ClientResponse response = contactResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, new Contact(0, "Health Check First Name", "Health Check Last Name", "000000000"));

        if (response.getStatus() == 201) {
            return Result.healthy();
        } else {
            return Result.unhealthy("New contact cannot be created!");
        }
    }
}
