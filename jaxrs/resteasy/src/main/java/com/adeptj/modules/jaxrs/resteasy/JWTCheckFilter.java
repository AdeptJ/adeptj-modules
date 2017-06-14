package com.adeptj.modules.jaxrs.resteasy;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

import static javax.ws.rs.Priorities.AUTHENTICATION;

/**
 * Gets the HTTP Authorization header from the request and checks for the JSon Web Token (the Bearer string).
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@Provider
@JWTCheck
@Priority(AUTHENTICATION)
public class JWTCheckFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JWTCheckFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        try {
            Jwts.parser().setSigningKey(SigningKeyProvider.INSTANCE.signingKey()).parseClaimsJws(StringUtils.substring(
                    requestContext.getHeaderString(HttpHeaders.AUTHORIZATION), "Bearer".length()));
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException ex) {
            LOGGER.error("Invalid JWT!!", ex);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
