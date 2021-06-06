package com.adeptj.modules.httpclient;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.FormRequestContent;
import org.eclipse.jetty.client.util.StringRequestContent;
import org.eclipse.jetty.util.Fields;

import java.util.Map;

import static com.adeptj.modules.httpclient.RestClientConstants.CONTENT_TYPE_JSON;

public class JettyRequestFactory {

    public static Request newRequest(HttpClient jettyClient, ClientRequest cr, HttpMethod httpMethod) {
        Request request = jettyClient.newRequest(cr.getUri()).method(httpMethod.toString());
        Map<String, String> headers = cr.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            request.headers(m -> headers.forEach(m::add));
        }
        Map<String, String> queryParams = cr.getQueryParams();
        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.forEach(request::param);
        }
        if (StringUtils.isNotEmpty(cr.getBody())) {
            request.body(new StringRequestContent(CONTENT_TYPE_JSON, cr.getBody()));
        } else {
            Map<String, String> formParams = cr.getFormParams();
            if (formParams != null && !formParams.isEmpty()) {
                Fields fields = new Fields();
                formParams.forEach(fields::put);
                request.body(new FormRequestContent(fields));
            }
        }
        return request;
    }
}
