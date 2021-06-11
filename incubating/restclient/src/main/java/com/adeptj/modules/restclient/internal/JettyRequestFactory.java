package com.adeptj.modules.restclient.internal;

import com.adeptj.modules.restclient.Assert;
import com.adeptj.modules.restclient.ClientRequest;
import com.adeptj.modules.restclient.HttpMethod;
import com.adeptj.modules.restclient.ObjectMappers;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.FormRequestContent;
import org.eclipse.jetty.client.util.StringRequestContent;
import org.eclipse.jetty.util.Fields;

import java.util.Map;

import static com.adeptj.modules.restclient.HttpMethod.GET;
import static com.adeptj.modules.restclient.RestClientConstants.CONTENT_TYPE_JSON;

class JettyRequestFactory {

    static <T, R> Request newRequest(HttpClient jettyClient, ClientRequest<T, R> cr) {
        HttpMethod method = cr.getMethod();
        Assert.notNull(method, "HttpMethod can't be null");
        Request request = jettyClient.newRequest(cr.getUri()).method(method.toString());
        // Handle headers
        Map<String, String> headers = cr.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            request.headers(m -> headers.forEach(m::add));
        }
        // Handle query params
        Map<String, String> queryParams = cr.getQueryParams();
        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.forEach(request::param);
        }
        // No body for http GET, return right away.
        if (method == GET) {
            return request;
        }
        T body = cr.getBody();
        if (body == null) {
            // check if it is a form post
            Map<String, String> formParams = cr.getFormParams();
            if (formParams != null && !formParams.isEmpty()) {
                Fields fields = new Fields();
                formParams.forEach(fields::put);
                request.body(new FormRequestContent(fields));
            }
            return request;
        }
        // check if a body is provided (either a direct String or an Object, if Object then serialize it to JSON).
        if (body instanceof String) {
            request.body(new StringRequestContent(CONTENT_TYPE_JSON, (String) body));
        } else {
            request.body(new StringRequestContent(CONTENT_TYPE_JSON, ObjectMappers.serialize(body)));
        }
        return request;
    }
}
