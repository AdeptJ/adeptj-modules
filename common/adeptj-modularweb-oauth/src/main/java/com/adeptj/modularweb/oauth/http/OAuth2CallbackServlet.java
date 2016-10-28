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
package com.adeptj.modularweb.oauth.http;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adeptj.modularweb.oauth.provider.api.OAuthProviderFactory;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

/**
 * OAuth2CallbackServlet.
 * 
 * @author Rakesh.Kumar, AdeptJ
 */
@Service
@Component
@Properties({ @Property(name = HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, value = "OAuth2CallbackServlet"),
		@Property(name = HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, value = "/oauth2/callback/*") })
public class OAuth2CallbackServlet extends HttpServlet {

	private static final long serialVersionUID = 4678395471803183796L;

	private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2CallbackServlet.class);

	@Reference
	private OAuthProviderFactory providerFactory;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.processRequest(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.processRequest(req, resp);
	}

	private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String code = req.getParameter("code");
		LOGGER.info("OAuth2 code: [{}]", code);
		String provider = StringUtils.substringAfterLast(req.getRequestURI(), "/");
		LOGGER.info("Provider: [{}]", provider);
		OAuth20Service oAuth2Service = this.providerFactory.getOAuth2Service(provider);
		OAuth2AccessToken token = oAuth2Service.getAccessToken(code);
		LOGGER.info("OAuth2AccessToken: [{}]", token);
		OAuthRequest oReq = new OAuthRequest(Verb.GET, "https://api.linkedin.com/v1/people/~?format=json",
				oAuth2Service);
		oAuth2Service.signRequest(token, oReq);
		Response oResp = oReq.send();
		LOGGER.info("Linkedin Profile: [{}]", oResp.getBody());
		resp.getOutputStream().write(oResp.getBody().getBytes(StandardCharsets.UTF_8));
	}

}
