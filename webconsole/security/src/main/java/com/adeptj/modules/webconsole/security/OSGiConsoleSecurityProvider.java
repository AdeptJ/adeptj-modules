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

package com.adeptj.modules.webconsole.security;

import org.apache.felix.webconsole.WebConsoleSecurityProvider;
import org.apache.felix.webconsole.WebConsoleSecurityProvider3;
import org.osgi.service.component.annotations.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static javax.servlet.http.HttpServletResponse.SC_FOUND;

/**
 * Felix {@link WebConsoleSecurityProvider} implementation.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(immediate = true, service = WebConsoleSecurityProvider.class)
public class OSGiConsoleSecurityProvider implements WebConsoleSecurityProvider3 {

	private static final String URL_TOOLS_LOGOUT = "/tools/logout";

	private static final String HEADER_LOC = "Location";

	private static final String ADMIN = "admin";
	
	private static final String ROLE_OSGI_ADMIN = "OSGiAdmin";

	/**
	 * Role [OSGiAdmin] is already set by Undertow SecurityHandler.
	 */
	@Override
	public boolean authenticate(HttpServletRequest request, HttpServletResponse response) {
		return request.isUserInRole(ROLE_OSGI_ADMIN);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object authenticate(String username, String password) {
		return ADMIN;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean authorize(Object user, String role) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		// Note: Semantics of this method states that Session invalidation should not happen here.
		// Not using response.sendRedirect due to exception handling we need to do, avoiding that.
		// Set the status to [302] and location header to [/tools/logout] so that browser could redirect there.
		// ProxyServlet will take care of Session invalidation later.
		response.setStatus(SC_FOUND);
		response.setHeader(HEADER_LOC, URL_TOOLS_LOGOUT);
	}

}
