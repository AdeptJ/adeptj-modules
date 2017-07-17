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

package com.adeptj.modules.aws.ses;

import com.adeptj.modules.aws.ses.api.EmailService;
import org.jboss.resteasy.annotations.Form;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static com.adeptj.modules.aws.ses.EmailResource.RESOURCE_BASE;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * REST Endpoint for sending email via AWS Simple Email Service.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Path("/aws/ses")
@Component(immediate = true, service = EmailResource.class, property = RESOURCE_BASE)
public class EmailResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailResource.class);

    static final String RESOURCE_BASE = "osgi.jaxrs.resource.base=aws-ses";

    @Reference
    private EmailService emailService;

    @POST
    @Path("/send")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Response sendEmail(@Form EmailForm form) {
        return Response.ok(this.emailService.sendEmail(new EmailRequest(form.getRecipient(),
                form.getSubject(),
                form.getBody())))
                .build();
    }

    @POST
    @Path("/send-async")
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Response sendEmailAsync(@Form EmailForm form) {
        this.emailService.sendEmailAsync(new EmailRequest(form.getRecipient(),
                form.getSubject(),
                form.getBody()));
        return Response.ok("Email sent asynchronously!!").build();
    }
}
