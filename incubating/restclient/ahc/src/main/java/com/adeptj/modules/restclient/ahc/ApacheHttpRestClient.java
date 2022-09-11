package com.adeptj.modules.restclient.ahc;

import com.adeptj.modules.restclient.core.AbstractRestClient;
import com.adeptj.modules.restclient.core.ClientRequest;
import com.adeptj.modules.restclient.core.ClientResponse;
import com.adeptj.modules.restclient.core.HttpMethod;
import com.adeptj.modules.restclient.core.RestClient;
import com.adeptj.modules.restclient.core.RestClientException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

@Component(service = RestClient.class)
public class ApacheHttpRestClient extends AbstractRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final CloseableHttpClient httpClient;

    public ApacheHttpRestClient() {
        this.httpClient = HttpClients.createDefault();
    }

    @Override
    public <T, R> ClientResponse<R> GET(ClientRequest<T, R> request) {
        this.ensureHttpMethod(request, HttpMethod.GET);
        return this.executeRequest(request);
    }

    @Override
    public <T, R> ClientResponse<R> POST(ClientRequest<T, R> request) {
        this.ensureHttpMethod(request, HttpMethod.POST);
        return this.executeRequest(request);
    }

    @Override
    public <T, R> ClientResponse<R> PUT(ClientRequest<T, R> request) {
        this.ensureHttpMethod(request, HttpMethod.PUT);
        return this.executeRequest(request);
    }

    @Override
    public <T, R> ClientResponse<R> DELETE(ClientRequest<T, R> request) {
        this.ensureHttpMethod(request, HttpMethod.DELETE);
        return this.executeRequest(request);
    }

    @Override
    public <T, R> ClientResponse<R> executeRequest(ClientRequest<T, R> request) {
        HttpUriRequest apacheRequest = ApacheRequestFactory.newRequest(request);
        try (CloseableHttpResponse response = this.httpClient.execute(apacheRequest)) {
            return ClientResponseFactory.newClientResponse(response, request.getResponseAs());
        } catch (Exception ex) {
            throw new RestClientException(ex);
        }
    }

    // <<----------------------------------------- OSGi Internal  ------------------------------------------>>

    @Deactivate
    protected void stop() {
        LOGGER.info("Stopping Apache HttpClient!");
        try {
            this.httpClient.close();
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
