package com.adeptj.modules.restclient.internal;

import com.adeptj.modules.restclient.ClientRequest;
import com.adeptj.modules.restclient.ObjectMappers;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

class RestClientLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String REQUEST_START = "<<==================== Request Processing Start ====================>>\n";

    private static final String REQUEST_END = "<<==================== Request Processing Complete ====================>>";

    private static final String RESPONSE_START = "<<==================== Response ====================>>\n";

    private static final String REQ_FMT
            = "\n{}\n Request ID: {}\n Request Method: {}\n Request URI: {}\n Request Headers:\n{}\n Request Body:\n {}";

    private static final String RESP_FMT
            = "\n{}\n Request ID: {}\n Response Status: {}\n Response Headers:\n{}\n Response Body:\n {}\n Total Time: {} milliseconds\n\n{}";

    static <T, R> String logRequest(ClientRequest<T, R> request, Request jettyRequest, String mdcReqIdAttrName) {
        String reqId = MDC.get(mdcReqIdAttrName);
        // Just in case MDC attribute is not set up by application code earlier.
        if (reqId == null) {
            reqId = UUID.randomUUID().toString();
        }
        LOGGER.info(REQ_FMT, REQUEST_START, reqId, request.getMethod(), request.getUri(),
                serializeHeaders(jettyRequest.getHeaders()),
                getBody(request));
        return reqId;
    }

    static void logResponse(String reqId, ContentResponse response, long executionTime) {
        LOGGER.info(RESP_FMT, RESPONSE_START, reqId, response.getStatus(), serializeHeaders(response.getHeaders()),
                new String(response.getContent(), StandardCharsets.UTF_8),
                TimeUnit.NANOSECONDS.toMillis(executionTime),
                REQUEST_END);
    }

    private static <T, R> String getBody(ClientRequest<T, R> request) {
        T body = request.getBody();
        if (body == null) {
            return "<<NO BODY>>";
        }
        return (body instanceof String) ? (String) body : ObjectMappers.serialize(body);
    }

    private static String serializeHeaders(HttpFields fields) {
        Map<String, String> headers = new HashMap<>();
        fields.forEach(f -> headers.put(f.getName(), f.getValue()));
        return ObjectMappers.serializePrettyPrint(headers);
    }
}
