/*
###############################################################################
#                                                                             #
#    Copyright 2016-2022, AdeptJ (http://www.adeptj.com)                      #
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
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;

class JettyRestClientLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String REQUEST_START = "<<==================== Request Processing Start ====================>>\n";

    private static final String REQUEST_END = "<<==================== Request Processing Complete ====================>>";

    private static final String RESPONSE_START = "<<==================== Response ====================>>\n";

    private static final String REQ_FMT
            = "\n{}\n Request ID: {}\n Request Method: {}\n Request URI: {}\n Request Headers:\n{}\n Request Body:\n{}";

    private static final String RESP_FMT
            = "\n{}\n Request ID: {}\n Response Status: {}\n Response Headers:\n{}\n Response Body:\n{}\n Total Time: {} milliseconds\n\n{}";

    static <T, R> String logRequest(ClientRequest<T, R> request, Request jettyRequest, String mdcReqIdAttrName) {
        String reqId = getReqId(mdcReqIdAttrName);
        LOGGER.info(REQ_FMT, REQUEST_START, reqId, request.getMethod(), request.getURI(),
                serializeHeaders(jettyRequest.getHeaders()),
                getBody(request));
        return reqId;
    }

    static void logResponse(String reqId, ContentResponse response, long executionTime) {
        LOGGER.info(RESP_FMT, RESPONSE_START, reqId, response.getStatus(), serializeHeaders(response.getHeaders()),
                new String(response.getContent(), UTF_8),
                TimeUnit.NANOSECONDS.toMillis(executionTime),
                REQUEST_END);
    }

    private static String getReqId(String mdcReqIdAttrName) {
        String reqId = MDC.get(mdcReqIdAttrName);
        // Just in case MDC attribute is not set up by application code earlier.
        if (reqId == null) {
            reqId = UUID.randomUUID().toString();
        }
        return reqId;
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
        fields.forEach(f -> headers.put(f.getName(), f.getValue()));
        return ObjectMappers.serializePrettyPrint(headers);
    }
}
