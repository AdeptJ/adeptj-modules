/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://adeptj.com)                               #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/
package com.adeptj.modules.commons.ds;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Path("/async")
@Component(immediate = true, service = AsyncController.class, property = "osgi.jaxrs.resource.base=/async")
public class AsyncController {

    private ExecutorService executor;

	@GET
	@Path("/hello/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public void hello(@Suspended AsyncResponse async, @PathParam("name") @Size(min = 3, max = 5) String name) {
        this.executor.submit(() -> {
            User user = new User();
            user.setUsername("rakeshk15");
            user.setFirstName("Rakesh");
            user.setLastName("Kumar");
            List<User> users = new ArrayList<>();
            users.add(user);
            async.resume(Response.ok(users).build());
        });

	}

	@Activate
	protected void activate() {
	    this.executor = Executors.newFixedThreadPool(5);
    }
}
