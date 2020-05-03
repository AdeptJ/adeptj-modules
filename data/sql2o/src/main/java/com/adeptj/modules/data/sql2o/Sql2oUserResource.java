package com.adeptj.modules.data.sql2o;

import com.adeptj.modules.jaxrs.core.JaxRSResource;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@JaxRSResource(name = "Sql2oResource")
@Path("/sql2o")
@Component(service = Sql2oUserResource.class)
public class Sql2oUserResource {

    private final Sql2oUserRepository userRepository;

    @Activate
    public Sql2oUserResource(@Reference Sql2oUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Path("/users")
    @GET
    @Produces(APPLICATION_JSON)
    public List<User> getUsers() {
        return this.userRepository.find(User.class, "select * from adeptj.users");
    }

    @Path("/user")
    @GET
    @Produces(APPLICATION_JSON)
    public User getUser() {
        NamedParam param1 = new NamedParam("contact", "1234567892");
        NamedParam param2 = new NamedParam("email", "john.doe3@johndoe.com");
        return this.userRepository.findOne(User.class,
                "select * from adeptj.users where MOBILE_NO=:contact AND EMAIL=:email", param1, param2);
    }
}
