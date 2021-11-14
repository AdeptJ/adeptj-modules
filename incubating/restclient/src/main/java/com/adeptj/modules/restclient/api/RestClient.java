/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://www.adeptj.com)                           #
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
package com.adeptj.modules.restclient.api;

import org.eclipse.jetty.client.HttpClient;
import org.osgi.annotation.versioning.ProviderType;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The RestClient.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ProviderType
public interface RestClient {

    <T, R> ClientResponse<R> GET(ClientRequest<T, R> request);

    <T, R> ClientResponse<R> POST(ClientRequest<T, R> request);

    <T, R> ClientResponse<R> PUT(ClientRequest<T, R> request);

    <T, R> ClientResponse<R> DELETE(ClientRequest<T, R> request);

    <T, R> ClientResponse<R> executeRequest(ClientRequest<T, R> request);

    <R> R doWithHttpClient(Function<HttpClient, R> function);

    void doWithHttpClient(Consumer<HttpClient> consumer);
}
