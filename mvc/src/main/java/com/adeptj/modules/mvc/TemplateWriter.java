package com.adeptj.modules.mvc;

import com.adeptj.modules.jaxrs.core.JaxRSProvider;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Component
@JaxRSProvider(name = "TemplateWriter")
@Produces("text/html")
public class TemplateWriter implements MessageBodyWriter<Template> {

    @Reference
    private TemplateEngine templateEngine;

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == Template.class;
    }

    @Override
    public void writeTo(Template template, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        this.templateEngine.process(template);
    }
}
