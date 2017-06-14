package com.adeptj.modules.jaxrs.resteasy;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.osgi.service.component.annotations.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

/**
 * Authenticator.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@Path("/auth")
@Component(immediate = true, service = Authenticator.class, property = "osgi.jaxrs.resource.base=auth")
public class Authenticator {

    @POST
    @Path("login")
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Response authenticate(@FormParam("username") String username, @FormParam("pwd") String pwd) {
        try {
            // First authenticate the user using the credentials provided but from where?
            // Now issue a token for the user
            return Response.ok().header(AUTHORIZATION, "Bearer " + this.issueToken(username)).build();
        } catch (Exception e) {
            return Response.status(UNAUTHORIZED).build();
        }
    }

    @GET
    @Path("jwt-check")
    @JWTCheck
    public Response withAuth() {
        return Response.ok().build();
    }

    private String issueToken(String login) {
        return Jwts.builder()
                .setSubject(login)
                .setIssuer("AdeptJ Runtime REST API")
                .setIssuedAt(new Date())
                .setExpiration(Date.from(LocalDateTime.now().plusMinutes(15l).atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS256, SigningKeyProvider.INSTANCE.signingKey())
                .compact();
    }
}
