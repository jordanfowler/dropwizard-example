package com.dwbook.phonebook.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import com.dwbook.phonebook.representations.Contact;
import org.skife.jdbi.v2.DBI;
import com.dwbook.phonebook.dao.ContactDAO;
import io.dropwizard.auth.Auth;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.ws.rs.core.Response.Status;

@Path("/contact")
@Produces(MediaType.APPLICATION_JSON)
public class ContactResource {
    private final ContactDAO contactDao;
    private final Validator validator;

    public ContactResource(DBI j, Validator v) {
        contactDao = j.onDemand(ContactDAO.class);
        validator = v;
    }

    @GET
    @Path("/{id}")
    public Response getContact(@PathParam("id") int id, @Auth Boolean isAuthenticated) {
        // retrieve information about the contact with the provided id
        Contact contact = contactDao.getContactById(id);
        
        return Response
              .ok(contact)
              .build();
    }

    @POST
    public Response createContact(@Valid Contact contact, @Auth Boolean isAuthenticated) throws URISyntaxException {
        // Validate the contact's data
//        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);
//
//        if (violations.size() > 0) {
//            // Validation errors occurred
//            ArrayList<String> validationMessages = new ArrayList<String>();
//            for (ConstraintViolation<Contact> violation : violations) {
//                validationMessages.add(violation.getPropertyPath().toString() + ": " + violation.getMessage());
//            }
//            return Response.status(Status.BAD_REQUEST).entity(validationMessages).build();
//        } else {
            // store the new contact 
            int newContactId = contactDao.createContact(contact.getFirstName(), contact.getLastName(), contact.getPhone());
            return Response
                    .created(new URI(String.valueOf(newContactId)))
                    .build();
//        }
        
    }

    @DELETE
    @Path("/{id}")
    public Response deleteContact(@PathParam("id") int id, @Auth Boolean isAuthenticated) {
        // delete the contact with the provided id
        contactDao.deleteContact(id);
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}")
    public Response updateContact(
            @PathParam("id") int id,
            @Valid Contact contact,
            @Auth Boolean isAuthenticated) throws URISyntaxException {
//        // Validate the contact's data
//        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);
//
//        if (violations.size() > 0) {
//            // Validation errors occurred
//            ArrayList<String> validationMessages = new ArrayList<String>();
//            for (ConstraintViolation<Contact> violation : violations) {
//                validationMessages.add(violation.getPropertyPath().toString() + ": " + violation.getMessage());
//            }
//            return Response.status(Status.BAD_REQUEST).entity(validationMessages).build();
//        } else {
            // update the contact with the provided ID
            contactDao.updateContact(id, contact.getFirstName(), contact.getLastName(), contact.getPhone());
            return Response
                    .ok(new Contact(id, contact.getFirstName(), contact.getLastName(), contact.getPhone()))
                    .header("Location", new URI("/contact/" + String.valueOf(id)))
                    .build();
//        }
    }
}
