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
package com.adeptj.modules.restclient.jetty;

import com.adeptj.modules.restclient.core.ClientRequest;
import com.adeptj.modules.restclient.core.util.ObjectMappers;
import org.eclipse.jetty.client.ContentResponse;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.adeptj.modules.restclient.core.RestClientConstants.HEADER_AUTHORIZATION;
import static com.adeptj.modules.restclient.core.RestClientConstants.REQUEST_END;
import static com.adeptj.modules.restclient.core.RestClientConstants.REQUEST_START;
import static com.adeptj.modules.restclient.core.RestClientConstants.REQ_FMT;
import static com.adeptj.modules.restclient.core.RestClientConstants.RESPONSE_START;
import static com.adeptj.modules.restclient.core.RestClientConstants.RESP_FMT;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Rakesh Kumar, AdeptJ
 */
class JettyRestClientLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private JettyRestClientLogger() {
    }

    static <T, R> void logRequest(String reqId, ClientRequest<T, R> request, Request jettyRequest) {
        LOGGER.info(REQ_FMT, REQUEST_START, reqId, request.getMethod(), request.getURI(),
                serializeHeaders(jettyRequest.getHeaders()),
                getBody(request));
    }

    static void logResponse(String reqId, ContentResponse response, long executionTime) {
        LOGGER.info(RESP_FMT, RESPONSE_START, reqId, response.getStatus(), serializeHeaders(response.getHeaders()),
                new String(response.getContent(), UTF_8),
                TimeUnit.NANOSECONDS.toMillis(executionTime),
                REQUEST_END);
    }

    private static <T, R> String getBody(ClientRequest<T, R> request) {
        String body = ObjectMappers.serialize(request.getBody());
        if (StringUtil.isBlank(body)) {
            return "<<NO BODY>>";
        }
        return body;
    }

    private static String serializeHeaders(HttpFields fields) {
        Map<String, String> headers = new HashMap<>();
        for (HttpField field : fields) {
            // Mask Authorization header.
            if (HEADER_AUTHORIZATION.equals(field.getName())) {
                headers.put(field.getName(), "XXXXXXXXXX");
            } else {
                headers.put(field.getName(), field.getValue());
            }
        }
        return ObjectMappers.serializePrettyPrint(headers);
    }
}
