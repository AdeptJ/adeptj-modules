package com.adeptj.modules.jaxrs.resteasy;

import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for use by JAX-RS resources wherever a JWT auth required.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@NameBinding
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface JWTCheck {
}
