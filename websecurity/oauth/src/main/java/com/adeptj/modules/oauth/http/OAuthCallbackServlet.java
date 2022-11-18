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

import com.adeptj.modules.oauth.provider.api.OAuthProviderFactory;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

/**
 * OAuth2CallbackServlet.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class OAuthCallbackServlet extends HttpServlet {

    private static final long serialVersionUID = 4678395471803183796L;

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuthCallbackServlet.class);

    private OAuthProviderFactory providerFactory;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = req.getParameter("code");
        LOGGER.info("OAuth2 code: [{}]", code);
        String provider = StringUtils.substringAfterLast(req.getRequestURI(), "/");
        LOGGER.info("Provider: [{}]", provider);
        OAuth20Service oAuth2Service = this.providerFactory.getOAuth2Service(provider);
        OAuth2AccessToken token = null;
        try {
            token = oAuth2Service.getAccessToken(code);
            LOGGER.info("OAuth2AccessToken: [{}]", token);
            OAuthRequest oReq = new OAuthRequest(Verb.GET, "https://api.linkedin.com/v1/people/~?format=json");
            oAuth2Service.signRequest(token, oReq);
            Response oResp = oAuth2Service.execute(oReq);
            LOGGER.info("Linkedin Profile: [{}]", oResp.getBody());
            resp.getOutputStream().write(oResp.getBody().getBytes(StandardCharsets.UTF_8));
        } catch (InterruptedException | ExecutionException ex) {
        }
    }

}
