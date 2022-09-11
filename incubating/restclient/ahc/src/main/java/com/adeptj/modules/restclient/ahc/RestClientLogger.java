package com.adeptj.modules.restclient.ahc;

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
import org.slf4j.MDC;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RestClientLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String REQUEST_START = "<<==================== Request Processing Start ====================>>\n";

    private static final String REQUEST_END = "<<==================== Request Processing Complete ====================>>";

    private static final String RESPONSE_START = "<<==================== Response ====================>>\n";

    private static final String REQ_FMT
            = "\n{}\n Request ID: {}\n Request Method: {}\n Request URI: {}\n Request Headers: {}\n Request Body: {}";

    private static final String RESP_FMT
            = "\n{}\n Request ID: {}\n Response Status: {}\n Response Headers: {}\n Response Body: {}\n Total Time: {} ms\n\n{}";

    static String logRequest(HttpUriRequest request, String mdcReqIdAttrName) {
        String body = getBody(request);
        String reqId = getReqId(mdcReqIdAttrName);
        LOGGER.info(REQ_FMT, REQUEST_START, reqId, request.getMethod(),
                request.getURI(),
                serializeReqHeaders(request),
                StringUtils.isEmpty(body) ? "<<NO BODY>>" : body);
        return reqId;
    }

    static <T> void logResponse(String reqId, ClientResponse<T> response, long executionTime) {
        LOGGER.info(RESP_FMT, RESPONSE_START, reqId, response.getStatus(),
                ObjectMappers.serializePrettyPrint(response.getHeaders()),
                (response.getContent() instanceof String ? response.getContent() : "<<SKIPPED>>"),
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

    private static String getBody(HttpUriRequest request) {
        String body = null;
        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest enclosingRequest = (HttpEntityEnclosingRequest) request;
            HttpEntity entity = enclosingRequest.getEntity();
            if (entity instanceof StringEntity) {
                StringEntity strEntity = (StringEntity) entity;
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

    private static String serializeReqHeaders(HttpUriRequest request) {
        Map<String, String> headers = new HashMap<>();
        Header[] allHeaders = request.getAllHeaders();
        if (allHeaders != null && allHeaders.length > 0) {
            for (Header header : allHeaders) {
                headers.put(header.getName(), header.getValue());
            }
        }
        return ObjectMappers.serializePrettyPrint(headers);
    }
}
