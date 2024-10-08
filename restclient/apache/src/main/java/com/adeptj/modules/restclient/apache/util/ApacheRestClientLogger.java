/*
###############################################################################
#                                                                             #
#    Copyright 2016-2024, AdeptJ (http://www.adeptj.com)                      #
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
package com.adeptj.modules.restclient.apache.util;

import com.adeptj.modules.restclient.core.ClientResponse;
import com.adeptj.modules.restclient.core.util.ObjectMappers;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.adeptj.modules.restclient.core.RestClientConstants.HEADER_AUTHORIZATION;
import static com.adeptj.modules.restclient.core.RestClientConstants.REQUEST_END;
import static com.adeptj.modules.restclient.core.RestClientConstants.REQUEST_START;
import static com.adeptj.modules.restclient.core.RestClientConstants.REQ_FMT;
import static com.adeptj.modules.restclient.core.RestClientConstants.RESPONSE_START;
import static com.adeptj.modules.restclient.core.RestClientConstants.RESP_FMT;

/**
 * @author Rakesh Kumar, AdeptJ
 */
public class ApacheRestClientLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void logRequest(String reqId, HttpUriRequest request) {
        String body = getBody(request);
        LOGGER.info(REQ_FMT, REQUEST_START, reqId, request.getMethod(),
                request.getURI(),
                serializeHeaders(request),
                StringUtils.isEmpty(body) ? "<<NO BODY>>" : body);
    }

    public static <T> void logResponse(String reqId, ClientResponse<T> response, long executionTime) {
        LOGGER.info(RESP_FMT, RESPONSE_START, reqId, response.getStatus(),
                ObjectMappers.serializePrettyPrint(response.getHeaders()),
                (response.getContent() instanceof String ? response.getContent() : "<<SKIPPED>>"),
                TimeUnit.NANOSECONDS.toMillis(executionTime),
                REQUEST_END);
    }

    private static String getBody(HttpUriRequest request) {
        String body = null;
        if (request instanceof HttpEntityEnclosingRequest enclosingRequest) {
            HttpEntity entity = enclosingRequest.getEntity();
            if (entity instanceof StringEntity strEntity) {
                try {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    IOUtils.copy(strEntity.getContent(), out);
                    body = out.toString(StandardCharsets.UTF_8);
                } catch (IOException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        }
        return body;
    }

    private static String serializeHeaders(HttpUriRequest request) {
        Map<String, String> headers = new HashMap<>();
        Header[] apacheRequestHeaders = request.getAllHeaders();
        if (apacheRequestHeaders != null) {
            for (Header header : apacheRequestHeaders) {
                // Mask Authorization header.
                if (HEADER_AUTHORIZATION.equals(header.getName())) {
                    headers.put(header.getName(), "XXXXXXXXXX");
                } else {
                    headers.put(header.getName(), header.getValue());
                }
            }
        }
        return ObjectMappers.serializePrettyPrint(headers);
    }

    private ApacheRestClientLogger() {
    }
}
