package com.adeptj.modules.restclient.ahc;

import com.adeptj.modules.restclient.core.ClientRequest;
import com.adeptj.modules.restclient.core.util.ObjectMappers;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.adeptj.modules.restclient.core.HttpMethod.POST;
import static java.nio.charset.StandardCharsets.UTF_8;

public class EntityEnclosingRequest extends HttpEntityEnclosingRequestBase {

    private final String method;

    <T, R> EntityEnclosingRequest(ClientRequest<T, R> request) {
        this.method = request.getMethod().toString();
        Map<String, String> formParams = request.getFormParams();
        T body = request.getBody();
        if (body != null) {
            String data = ObjectMappers.serialize(body);
            StringEntity entity = new StringEntity(data, UTF_8);
            entity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            this.setEntity(entity);
        } else if (request.getMethod() == POST && formParams != null && !formParams.isEmpty()) {
            List<NameValuePair> form = new ArrayList<>();
            for (Map.Entry<String, String> entry : formParams.entrySet()) {
                form.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            this.setEntity(new UrlEncodedFormEntity(form, UTF_8));
        }
    }

    @Override
    public String getMethod() {
        return this.method;
    }
}
