package com.adeptj.modules.security.oauth.jaxrs;

import com.adeptj.modules.jaxrs.api.JaxRSResource;
import com.adeptj.modules.security.oauth.OAuthProviderService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.URI;

/**
 * The JAX-RS resource for handling OAuth authorization.
 *
 * @author Rakesh Kumar, AdeptJ
 */
@JaxRSResource(name = "OAuthAuthorizationResource")
@Path("/oauth/authorize/{provider}")
@Component(service = OAuthAuthorizationResource.class)
public class OAuthAuthorizationResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final OAuthProviderService providerService;

    @Activate
    public OAuthAuthorizationResource(@Reference OAuthProviderService providerService) {
        this.providerService = providerService;
    }

    @GET
    public Response redirectToAuthorizationUrl(@PathParam("provider") String providerName) {
        String authorizationUrl = this.providerService.getProvider(providerName).getAuthorizationUrl();
        LOGGER.info("Redirecting to ({}) authorization url!", providerName);
        return Response.seeOther(URI.create(authorizationUrl)).build();
    }
}
