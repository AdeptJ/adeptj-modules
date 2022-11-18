/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://www.adeptj.com)                           #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/

package com.adeptj.modules.security.core.internal;

import com.adeptj.modules.commons.utils.RandomGenerators;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.servlet.whiteboard.Preprocessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

import static com.adeptj.modules.security.core.SecurityConstants.KEY_REQUEST_ID;

/**
 * Sets a random UUID in {@link MDC}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(service = Preprocessor.class)
public class RequestIdInitializer implements Preprocessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String PROCESSING_REQUEST_MSG = "Processing [{}] request for [{}]";

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        if (LOGGER.isDebugEnabled()) {
            HttpServletRequest request = (HttpServletRequest) req;
            LOGGER.debug(PROCESSING_REQUEST_MSG, request.getMethod(), request.getRequestURI());
        }
        try {
            MDC.put(KEY_REQUEST_ID, RandomGenerators.uuidString());
            chain.doFilter(req, resp);
        } finally {
            MDC.remove(KEY_REQUEST_ID);
        }
    }
}
