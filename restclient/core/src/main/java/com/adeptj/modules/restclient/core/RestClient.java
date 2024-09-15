/*
###############################################################################
#                                                                             #
#    Copyright 2016-2024, AdeptJ (http://www.adeptj.com)                      #
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
package com.adeptj.modules.restclient.core;

import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ProviderType;

/**
 * The RestClient.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ProviderType
public interface RestClient {

    <T, R> ClientResponse<R> GET(@NotNull ClientRequest<T, R> request);

    <T, R> ClientResponse<R> POST(@NotNull ClientRequest<T, R> request);

    <T, R> ClientResponse<R> PUT(@NotNull ClientRequest<T, R> request);

    <T, R> ClientResponse<R> DELETE(@NotNull ClientRequest<T, R> request);

    <T, R> ClientResponse<R> executeRequest(@NotNull ClientRequest<T, R> request);

    /**
     * Provides the internal http client.
     *
     * @return the internal http client
     */
    <T> T unwrap(@NotNull Class<T> type);
}
