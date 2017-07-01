/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://www.adeptj.com)                           #
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

package com.adeptj.modules.commons.aws.messaging;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;

/**
 * REST Endpoint for testing AWS Sms and Email service.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Path("/aws")
@Component(immediate = true, service = AWSMessagingController.class, property = "osgi.jaxrs.resource.base=aws")
public class AWSMessagingController {

    @Reference
    private AWSMessagingService messagingService;

    @POST
    @Path("/msg/sms")
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Response sendSMS(
            @FormParam("countryCode") String countryCode,
            @FormParam("mobile") String mobile,
            @FormParam("msg") String msg) {
        Map<String, String> data = new HashMap<>();
        data.put("mobNo", countryCode + mobile);
        data.put("message", msg);
        this.messagingService.sendMessage(MessageType.SMS, data);
        return Response.ok("SMS sent!!").build();
    }

    @POST
    @Path("/msg/email")
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Response sendEmail(
            @FormParam("emailId") String emailId,
            @FormParam("subject") String subject,
            @FormParam("msg") String msg) {
        Map<String, String> data = new HashMap<>();
        data.put("recipient", emailId);
        data.put("subject", subject);
        data.put("message", msg);
        this.messagingService.sendMessage(MessageType.EMAIL, data);
        return Response.ok("Email sent!!").build();
    }
}
