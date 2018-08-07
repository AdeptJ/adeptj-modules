package com.adeptj.modules.security.core;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.whiteboard.Preprocessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

@Component(service = Preprocessor.class)
public class DefaultPreprocessor implements Preprocessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        LOGGER.info("Processing request: {}", request);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
