package com.adeptj.modules.commons.ds;

import org.osgi.service.component.annotations.Component;

import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/qci")
@Component(immediate = true, service = QCIController.class, property = "osgi.jaxrs.resource.base=api")
public class QCIController {

	@GET
	@Path("/api/hello/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public User hello(@PathParam("name") @Size(min = 3, max = 5) String name) {
        User user = new User();
        user.setUsername("rakeshk15");
        user.setFirstName("Rakesh");
        user.setLastName("Kumar");
        return user;
	}
}
