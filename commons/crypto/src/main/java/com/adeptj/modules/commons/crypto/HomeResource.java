package com.adeptj.modules.commons.crypto;

import com.adeptj.modules.jaxrs.core.JaxRSResource;
import com.adeptj.modules.mvc.Template;
import com.adeptj.modules.mvc.TemplateContext;
import org.osgi.service.component.annotations.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

@Path("/template")
@JaxRSResource(name = "home")
@Component(service = HomeResource.class)
public class HomeResource {

    @Path("/home")
    @GET
    public Template render(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        return Template.using("home", TemplateContext.builder()
                .request(request)
                .response(response)
                .build());
    }
}
