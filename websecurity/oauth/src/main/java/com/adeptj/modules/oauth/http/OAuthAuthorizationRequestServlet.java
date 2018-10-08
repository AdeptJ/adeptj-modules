/*
 * =============================================================================
 *
 * Copyright (c) 2016 AdeptJ
 * Copyright (c) 2016 Rakesh Kumar <irakeshk@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * =============================================================================
 */
package com.adeptj.modules.oauth.http;

import com.adeptj.modules.oauth.common.OAuthProvider;
import com.adeptj.modules.oauth.provider.api.OAuthProviderFactory;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * OAuth2AuthorizationRequestServlet.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class OAuthAuthorizationRequestServlet extends HttpServlet {

    private static final long serialVersionUID = -7136335819267831703L;

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuthAuthorizationRequestServlet.class);

    private OAuthProviderFactory providerFactory;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String providerName = StringUtils.substringAfterLast(req.getRequestURI(), "/");
        LOGGER.info("Authz request for provider: [{}]", providerName);
        OAuthProvider provider = this.providerFactory.getProvider(providerName);
        LOGGER.info("OAuthProvider: [{}]", provider);
        OAuth20Service oauth2Service = this.providerFactory.getOAuth2Service(providerName);
        if (oauth2Service == null) {
            oauth2Service = new ServiceBuilder(provider.getApiKey())
                    .apiSecret(provider.getApiSecret())
                    .callback(provider.getCallbackURL())
                    .build(provider.getApi());
            this.providerFactory.addOAuth2Service(providerName, oauth2Service);
        }
        String authorizationUrl = oauth2Service.getAuthorizationUrl();
        LOGGER.info("Authz URL: [{}]", authorizationUrl);
        resp.sendRedirect(authorizationUrl);
    }

}
