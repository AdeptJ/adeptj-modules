package com.adeptj.modules.restclient.internal;

import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.invoke.MethodHandles;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.adeptj.modules.restclient.RestClientConstants.REST_CLIENT_REQ_ID;

class RestClientLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String REQUEST_START = "<<==================== Request Processing Start ====================>>\n";

    private static final String REQUEST_END = "<<==================== Request Processing Complete ====================>>";

    private static final String RESPONSE_START = "<<==================== Response ====================>>\n";

    private static final String REQ_FMT
            = "\n{}\n Request ID: {}\n Request Method: {}\n Request URI: {}\n Request Headers: {}\n Request Body: {}";

    private static final String RESP_FMT
            = "\n{}\n Request ID: {}\n Response Status: {}\n Response Headers: {}\n Response Body: {}\n Total Time: {} ms\n\n{}";

    static void logRequest(Request request) {
        LOGGER.info(REQ_FMT, REQUEST_START, MDC.get(REST_CLIENT_REQ_ID), request.getMethod(),
                request.getURI(),
                request.getHeaders(),
                "<<NO BODY>>");
    }

    static void logResponse(ContentResponse response, long executionTime) {
        LOGGER.info(RESP_FMT, RESPONSE_START, MDC.get(REST_CLIENT_REQ_ID), response.getStatus(),
                response.getHeaders(),
                "<<SKIPPED>>",
                TimeUnit.NANOSECONDS.toMillis(executionTime),
                REQUEST_END);
    }

    static void initReqId() {
        String reqId = MDC.get("REQ_ID");
        if (reqId == null) {
            MDC.put(REST_CLIENT_REQ_ID, UUID.randomUUID().toString());
        }
    }

    static void removeReqId() {
        String reqId = MDC.get(REST_CLIENT_REQ_ID);
        if (reqId != null) {
            MDC.remove(REST_CLIENT_REQ_ID);
        }
    }
}
