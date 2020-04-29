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

package com.adeptj.modules.aws.sns;

import com.adeptj.modules.aws.sns.api.SmsService;
import com.adeptj.modules.jaxrs.core.RequiresAuthentication;
import org.jboss.resteasy.annotations.Form;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static com.adeptj.modules.aws.sns.SmsResource.RESOURCE_BASE;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * REST Endpoint for testing AWS Sms and Email service.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Path("/aws/sns")
@Component(immediate = true, service = SmsResource.class, property = RESOURCE_BASE)
public class SmsResource {

    static final String RESOURCE_BASE = "osgi.jaxrs.resource.base=aws-sns";

    @Reference
    private SmsService smsService;

    @POST
    @Path("/send")
    @Produces(APPLICATION_JSON)
    @Consumes(APPLICATION_FORM_URLENCODED)
    @RequiresAuthentication
    public Response sendEmail(@Form SmsForm form) {
        return Response.ok(this.smsService.sendSms(new SmsRequest(form.getCountryCode(), form.getPhoneNumber(),
                form.getMessage())))
                .build();
    }

    @POST
    @Path("/send-async")
    @Consumes(APPLICATION_FORM_URLENCODED)
    @RequiresAuthentication
    public Response sendEmailAsync(@Form SmsForm form) {
        this.smsService.sendSmsAsync(new SmsRequest(form.getCountryCode(), form.getPhoneNumber(), form.getMessage()));
        return Response.ok("Sms sent asynchronously!!").build();
    }

}
