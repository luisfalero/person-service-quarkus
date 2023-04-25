package org.lfalero;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

import io.quarkus.panache.common.Sort;

@Path("/person")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource {

    @GET
    public List<PersonEntity> getAll() throws Exception {
        return PersonEntity.findAll(Sort.ascending("last_name")).list();
    }

    @GET
    @Path("{id}")
    public PersonEntity getPerson(@PathParam Long id) throws Exception {
        if(!Objects.nonNull(id)) throw new WebApplicationException("id must not be NULL");
        PersonEntity personEntity = PersonEntity.findById(id);
        if(!Objects.nonNull(personEntity)) throw new WebApplicationException("There is no person with id = " + id);
        return personEntity;
    }

    @POST
    @Transactional
    public Response create(PersonEntity personEntity) {
        if (!Objects.nonNull(personEntity) || Objects.nonNull(personEntity.id))
            throw new WebApplicationException("id != null");
        personEntity.persist();
        return Response.ok(personEntity).status(200).build();
    }

    @PUT
    @Transactional
    @Path("{id}")
    public PersonEntity update(@PathParam Long id, PersonEntity personEntity) {
        PersonEntity entity = PersonEntity.findById(id);
        if (!Objects.nonNull(entity)) {
            throw new WebApplicationException("Person with id of " + id + " does not exist.", 404);
        }
        if(Objects.nonNull(personEntity.getSalutation())) entity.setSalutation(personEntity.getSalutation());
        if(Objects.nonNull(personEntity.getFirstName())) entity.setFirstName(personEntity.getFirstName());
        if(Objects.nonNull(personEntity.getLastName())) entity.setLastName(personEntity.getLastName());
        return entity;
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam Long id) {
        PersonEntity personEntity = PersonEntity.findById(id);
        if (!Objects.nonNull(personEntity)) {
            throw new WebApplicationException("Person with id of " + id + " does not exist.", 404);
        }
        personEntity.delete();
        return Response.status(204).build();
    }

    @GET
    @Path("count")
    public Long count() {
        return PersonEntity.count();
    }

    @GET
    @Path("search/{salutation}")
    public List<PersonEntity> getPersonBySalutation(@PathParam String salutation) throws Exception {
        return PersonEntity.find("salutation", salutation).list();
    }
}
