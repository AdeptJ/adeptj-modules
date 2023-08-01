package com.adeptj.modules.security.oauth.jaxrs;

import com.adeptj.modules.jaxrs.api.JaxRSResource;
import com.adeptj.modules.security.oauth.OAuthProviderService;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

@JaxRSResource(name = "oauth2-callback")
@Path("/oauth2/callback")
@Component(service = OAuthCallbackResource.class)
public class OAuthCallbackResource extends BaseOAuthResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final List<OAuthProviderService> providers;

    public OAuthCallbackResource() {
        this.providers = new ArrayList<>();
    }

    @Path("/{provider}")
    @GET
    public Response getAuthorizationUrl(@PathParam("provider") String provider, @QueryParam("code") String code) {
        // OAuthProviderService are dynamically bind so store the current list in a temp var.
        List<OAuthProviderService> tempProviders = this.providers;
        OAuthProviderService providerService = this.resolveOAuthProviderService(provider, tempProviders);
        try (OAuth20Service service = providerService.getOAuthProvider().getService()) {
            OAuth2AccessToken token = service.getAccessToken(code);
            String accessToken = token.getAccessToken();
            return Response.ok().entity(accessToken).build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return Response.ok().entity("no access token!!").build();
    }

    @Reference(service = OAuthProviderService.class, cardinality = MULTIPLE, policy = DYNAMIC)
    protected void bindOAuthProviderService(OAuthProviderService providerService) {
        this.providers.add(providerService);
    }

    protected void unbindOAuthProviderService(OAuthProviderService providerService) {
        this.providers.remove(providerService);
    }
}