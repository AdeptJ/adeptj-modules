package com.adeptj.modules.restclient.internal;

import com.adeptj.modules.restclient.api.ClientRequest;
import com.adeptj.modules.restclient.api.HttpMethod;
import com.adeptj.modules.restclient.util.Assert;
import com.adeptj.modules.restclient.util.ObjectMappers;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.FormRequestContent;
import org.eclipse.jetty.client.util.StringRequestContent;
import org.eclipse.jetty.util.Fields;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.adeptj.modules.restclient.RestClientConstants.CONTENT_TYPE_JSON;
import static com.adeptj.modules.restclient.api.HttpMethod.GET;

class JettyRequestFactory {

    static <T, R> Request newRequest(HttpClient jettyClient, ClientRequest<T, R> request) {
        HttpMethod method = request.getMethod();
        Assert.notNull(method, "HttpMethod can't be null");
        Request jettyRequest = jettyClient.newRequest(request.getUri()).method(method.toString());
        // 1. Handle timeout
        if (request.getTimeout() > 0) {
            jettyRequest.timeout(request.getTimeout(), TimeUnit.MILLISECONDS);
        }
        // 2. Handle headers
        Map<String, String> headers = request.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            jettyRequest.headers(m -> headers.forEach(m::add));
        }
        // 3. Handle query params
        Map<String, String> queryParams = request.getQueryParams();
        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.forEach(jettyRequest::param);
        }
        // 4. No body for http GET, return right away.
        if (method == GET) {
            return jettyRequest;
        }
        // 5. Handle Form Post - application/x-www-form-urlencoded
        Map<String, String> formParams = request.getFormParams();
        if (formParams != null && !formParams.isEmpty()) {
            Fields fields = new Fields();
            formParams.forEach(fields::put);
            jettyRequest.body(new FormRequestContent(fields));
            return jettyRequest;
        }
        // 6. Handle Body, a JSON string or an Object which will be serialized to JSON.
        T body = request.getBody();
        if (body != null) {
            if (body instanceof String) {
                jettyRequest.body(new StringRequestContent(CONTENT_TYPE_JSON, (String) body));
            } else {
                jettyRequest.body(new StringRequestContent(CONTENT_TYPE_JSON, ObjectMappers.serialize(body)));
            }
        }
        return jettyRequest;
    }
}
