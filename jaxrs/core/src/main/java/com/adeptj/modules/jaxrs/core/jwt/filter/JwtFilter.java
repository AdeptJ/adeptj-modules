package com.adeptj.modules.jaxrs.core.jwt.filter;

import com.adeptj.modules.jaxrs.api.JaxRSProvider;
import com.adeptj.modules.jaxrs.core.jwt.JwtSecurityContext;
import com.adeptj.modules.jaxrs.core.jwt.resource.JwtExtractor;
import com.adeptj.modules.security.jwt.JwtClaims;
import com.adeptj.modules.security.jwt.JwtService;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.lang.invoke.MethodHandles;

import static com.adeptj.modules.jaxrs.core.jwt.filter.JwtFilter.FILTER_NAME;
import static jakarta.ws.rs.Priorities.AUTHENTICATION;
import static org.osgi.service.component.annotations.ReferenceCardinality.OPTIONAL;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

/**
 * This filter will kick in for all JaxRS resource classes and methods.
 * Filter will try to extract the Jwt from cookies if JwtCookieConfig#enabled returns true, if false then the Jwt is
 * extracted from HTTP Authorization header.
 * <p>
 * A Cookie named as per configuration should be present in request.
 * <p>
 * If a non-null Jwt is resolved then verify it using {@link JwtService}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@JaxRSProvider(name = FILTER_NAME)
@Priority(AUTHENTICATION)
@Provider
@Component(immediate = true)
public class JwtFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    static final String FILTER_NAME = "JaxRS.Security.JwtFilter";

    private JwtService jwtService;

    /**
     * Following steps are executed inside this method.
     * <p>
     * 1. Checks if the {@link JwtService} is null, if so, then do nothing.
     * 2. Extract Jwt from request (either from cookies or headers), if Jwt is null then do nothing.
     * 3. Verify Jwt using {@link JwtService}, if a null {@link JwtClaims} is returned then do nothing.
     * 4. When there is a non-null {@link JwtClaims} returned then create a {@link JwtSecurityContext} using the returned
     * claims and set this in the {@link ContainerRequestContext} to be used by other filters in chain as well as JaxRS resources.
     *
     * @param requestContext the JaxRS request context
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        JwtService tokenService = this.jwtService;
        if (tokenService == null) {
            return;
        }
        String jwt = JwtExtractor.extract(requestContext);
        if (StringUtils.isEmpty(jwt)) {
            return;
        }
        JwtClaims claims = tokenService.verifyJwt(jwt);
        if (claims == null) {
            return;
        }
        SecurityContext oldSecurityContext = requestContext.getSecurityContext();
        if (oldSecurityContext instanceof JwtSecurityContext) {
            LOGGER.warn("Current SecurityContext is already an instance of JwtSecurityContext!");
        }
        boolean httpsRequest = oldSecurityContext.isSecure();
        requestContext.setSecurityContext(new JwtSecurityContext(claims, httpsRequest));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("JwtSecurityContext initialized and set in ContainerRequestContext!");
        }
    }

    // <<------------------------------------- OSGi Internal  -------------------------------------->>

    @Reference(service = JwtService.class, cardinality = OPTIONAL, policy = DYNAMIC)
    protected void bindJwtService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    protected void unbindJwtService(JwtService jwtService) {
        if (this.jwtService == jwtService) {
            this.jwtService = null;
        }
    }
}
