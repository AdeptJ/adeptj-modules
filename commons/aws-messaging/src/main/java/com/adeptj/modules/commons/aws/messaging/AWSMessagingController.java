package com.adeptj.modules.commons.aws.messaging;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/aws")
@Component(immediate = true, service = AWSMessagingController.class, property = "osgi.jaxrs.resource.base=aws")
public class AWSMessagingController {

    @Reference
    private AWSMessagingService messagingService;

	@GET
	@Path("/messaging/sms/{mob}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> sendSMS(@PathParam("mob") String mob) {
        User user = new User();
        user.setUsername("rakeshk15");
        user.setFirstName("Rakesh");
        user.setLastName("Kumar");
        List<User> users = new ArrayList<>();
        users.add(user);

        // Send msg.
        Map<String, String> data = new HashMap<>();
        data.put("mobNo", mob);
        data.put("message", "Hi There, from AdeptJ!!");
        this.messagingService.sendMessage(MessageType.SMS, data);
        return users;
	}

    @GET
    @Path("/messaging/email/{emailId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<User> sendEmail(@PathParam("emailId") String emailId) {
        User user = new User();
        user.setUsername("rakeshk15");
        user.setFirstName("Rakesh");
        user.setLastName("Kumar");
        List<User> users = new ArrayList<>();
        users.add(user);

        // Send msg.
        Map<String, String> data = new HashMap<>();
        data.put("recipient", emailId);
        data.put("subject", "AdeptJ Runtime");
        data.put("message", "Hi There, from AdeptJ!!");
        this.messagingService.sendMessage(MessageType.EMAIL, data);
        return users;
    }
}
