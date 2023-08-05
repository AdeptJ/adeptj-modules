package com.adeptj.modules.security.oauth.jaxrs;

import com.adeptj.modules.jaxrs.api.JaxRSResource;
import com.adeptj.modules.security.oauth.OAuthAccessToken;
import com.adeptj.modules.security.oauth.OAuthAccessTokenConsumer;
import com.adeptj.modules.security.oauth.OAuthProviderService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.net.URI;

/**
 * The JAX-RS resource for handling OAuth callback with authorization code.
 *
 * @author Rakesh Kumar, AdeptJ
 */
@JaxRSResource(name = "OAuthCallbackResource")
@Path("/oauth/callback/{provider}")
@Component(service = OAuthCallbackResource.class)
public class OAuthCallbackResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final OAuthAccessTokenConsumer accessTokenConsumer;

    private final OAuthProviderService providerService;

    @Activate
    public OAuthCallbackResource(@Reference OAuthProviderService providerService,
                                 @Reference OAuthAccessTokenConsumer accessTokenConsumer) {
        this.providerService = providerService;
        this.accessTokenConsumer = accessTokenConsumer;
    }

    @GET
    public Response handleCallback(@PathParam("provider") String providerName, @QueryParam("code") String code) {
        LOGGER.info("Handling callback from ({})", providerName);
        OAuthAccessToken accessToken = this.providerService.getProvider(providerName).getAccessToken(code);
        String targetLocation = this.accessTokenConsumer.consume(accessToken);
        LOGGER.info("Redirecting to target location({}) provided by the OAuthAccessTokenConsumer!", targetLocation);
        return Response.seeOther(URI.create(targetLocation)).build();
    }
}
