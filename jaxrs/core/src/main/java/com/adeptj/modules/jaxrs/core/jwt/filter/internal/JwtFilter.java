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

import static com.adeptj.modules.jaxrs.core.JaxRSConstants.KEY_JWT_CLAIMS;
import static javax.ws.rs.Priorities.AUTHENTICATION;
import static org.osgi.service.component.annotations.ReferenceCardinality.OPTIONAL;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

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

    @Override
    public void filter(ContainerRequestContext requestContext) {
        JwtService jwtSvc = this.jwtService;
        if (jwtSvc == null) {
            requestContext.setSecurityContext(new AnonymousSecurityContext(requestContext));
            return;
        }
        JwtClaims claims;
        String jwt = JwtExtractor.extract(requestContext);
        if (StringUtils.isEmpty(jwt) || (claims = jwtSvc.verifyJwt(jwt)) == null) {
            requestContext.setSecurityContext(new AnonymousSecurityContext(requestContext));
            return;
        }
        requestContext.setProperty(KEY_JWT_CLAIMS, claims);
        requestContext.setSecurityContext(new JwtSecurityContext(requestContext, claims.asMap()));
    }
}
