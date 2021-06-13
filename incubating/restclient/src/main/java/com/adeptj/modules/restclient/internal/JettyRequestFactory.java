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

import static com.adeptj.modules.restclient.RestClientConstants.CONTENT_TYPE_JSON;
import static com.adeptj.modules.restclient.api.HttpMethod.GET;

class JettyRequestFactory {

    static <T, R> Request newRequest(HttpClient jettyClient, ClientRequest<T, R> cr) {
        HttpMethod method = cr.getMethod();
        Assert.notNull(method, "HttpMethod can't be null");
        Request request = jettyClient.newRequest(cr.getUri()).method(method.toString());
        // 1. Handle headers
        Map<String, String> headers = cr.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            request.headers(m -> headers.forEach(m::add));
        }
        // 2. Handle query params
        Map<String, String> queryParams = cr.getQueryParams();
        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.forEach(request::param);
        }
        // 3. No body for http GET, return right away.
        if (method == GET) {
            return request;
        }
        // 4. Handle Form Post - application/x-www-form-urlencoded
        Map<String, String> formParams = cr.getFormParams();
        if (formParams != null && !formParams.isEmpty()) {
            Fields fields = new Fields();
            formParams.forEach(fields::put);
            request.body(new FormRequestContent(fields));
            return request;
        }
        // 5. Handle Body, a JSON string or an Object which will be serialized to JSON.
        T body = cr.getBody();
        if (body != null) {
            if (body instanceof String) {
                request.body(new StringRequestContent(CONTENT_TYPE_JSON, (String) body));
            } else {
                request.body(new StringRequestContent(CONTENT_TYPE_JSON, ObjectMappers.serialize(body)));
            }
        }
        return request;
    }
}
