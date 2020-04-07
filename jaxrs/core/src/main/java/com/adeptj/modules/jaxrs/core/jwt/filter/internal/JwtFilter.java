package com.adeptj.modules.jaxrs.core.jwt.filter.internal;

import com.adeptj.modules.jaxrs.core.AnonymousSecurityContext;
import com.adeptj.modules.jaxrs.core.JaxRSProvider;
import com.adeptj.modules.jaxrs.core.jwt.JwtExtractor;
import com.adeptj.modules.jaxrs.core.jwt.JwtSecurityContext;
import com.adeptj.modules.security.jwt.JwtClaims;
import com.adeptj.modules.security.jwt.JwtService;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.Priorities.AUTHENTICATION;
import static org.osgi.service.component.annotations.ReferenceCardinality.OPTIONAL;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

/**
 * This filter will kick in for all JaxRS resource classes and methods.
 * Filter will try to extract the Jwt from cookies if JwtCookieConfig#enabled returns true, if false then the Jwt is
 * extracted from HTTP Authorization header.
 * <p>
 * A Cookie named as per configuration should be present in request.
 * <p>
 * If a non null Jwt is resolved then verify it using {@link JwtService}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@JaxRSProvider(name = "JaxRS SecurityFilter")
@Priority(AUTHENTICATION)
@Provider
@Component(immediate = true)
public class JwtFilter implements ContainerRequestFilter {

    /**
     * The optionally referenced {@link JwtService}.
     */
    @Reference(cardinality = OPTIONAL, policy = DYNAMIC, policyOption = GREEDY)
    private volatile JwtService jwtService;

    /**
     * 1. Checks if the {@link JwtService} is null, if so, then sets an {@link AnonymousSecurityContext} in the {@link ContainerRequestContext}.
     * 2. Extract Jwt from request (either from cookies or headers),
     * if Jwt is null then then sets an {@link AnonymousSecurityContext} in the {@link ContainerRequestContext}.
     * 3. Verify Jwt using {@link JwtService}, if a null {@link JwtClaims} is returned
     * then sets an {@link AnonymousSecurityContext} in the {@link ContainerRequestContext}.
     * 4. When there is a non null {@link JwtClaims} returned then create a {@link JwtSecurityContext} using the returned claims
     * and set this in the {@link ContainerRequestContext} to be used by other filters in chain as well as JaxRS resources.
     *
     * @param requestContext the JaxRS request context
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        JwtService jwtVerificationService = this.jwtService;
        if (jwtVerificationService == null) {
            requestContext.setSecurityContext(new AnonymousSecurityContext(requestContext));
            return;
        }
        JwtClaims claims;
        String jwt = JwtExtractor.extract(requestContext);
        if (StringUtils.isEmpty(jwt) || (claims = jwtVerificationService.verifyJwt(jwt)) == null) {
            requestContext.setSecurityContext(new AnonymousSecurityContext(requestContext));
            return;
        }
        requestContext.setSecurityContext(new JwtSecurityContext(requestContext, claims.asMap(), claims.isExpired()));
    }
}
