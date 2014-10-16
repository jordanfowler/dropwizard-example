package com.dwbook.phonebook.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import com.dwbook.phonebook.representations.Contact;
import com.sun.jersey.api.client.*;

@Path("/client")
@Produces(MediaType.TEXT_PLAIN)
public class ClientResource {
    private final Client client;
    
    public ClientResource(Client client) {
        this.client = client;
    }

    @GET
    @Path("showContact")
    public String showContact(@QueryParam("id") int id) {
        WebResource contactResource = client.resource("http://localhost:8080/contact/" + id);
        Contact c = contactResource.get(Contact.class);
        String output = "ID: " + id +
                        "\n First Name: " + c.getFirstName() +
                        "\n Last Name: " + c.getLastName() +
                        "\n Phone: " + c.getPhone();

        return output;
    }

    @GET
    @Path("newContact")
    public Response newContact(@QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName, @QueryParam("phone") String phone) {
        WebResource contactResource = client.resource("http://localhost:8080/contact");
        ClientResponse response = contactResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, new Contact(0, firstName, lastName, phone));

        if (response.getStatus() == 201) {
            // Success
            return Response.status(302).entity("Created Contact: " + response.getHeaders().getFirst("Location")).build();
        } else {
            // Failure
            return Response.status(422).entity(response.getEntity(String.class)).build();
        }
    }

    @GET
    @Path("updateContact")
    public Response updateContact(@QueryParam("id") int id, @QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName, @QueryParam("phone") String phone) {
        WebResource contactResource = client.resource("http://localhost:8080/contact/" + id);
        ClientResponse response = contactResource.type(MediaType.APPLICATION_JSON).put(ClientResponse.class, new Contact(id, firstName, lastName, phone));

        if (response.getStatus() == 200) {
            // Success
            return Response.status(302).entity("Updated Contact: " + response.getHeaders().getFirst("Location")).build();
        } else {
            // Failure
            return Response.status(422).entity(response.getEntity(String.class)).build();
        }
    }

    @GET
    @Path("deleteContact")
    public Response deleteContact(@QueryParam("id") int id) {
        WebResource contactResource = client.resource("http://localhost:8080/contact/" + id);
        ClientResponse response = contactResource.delete(ClientResponse.class);

        return Response.noContent().entity("Deleted Contact").build();
    }
}
