package com.adeptj.modularweb.webconsole.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.webconsole.WebConsoleSecurityProvider;
import org.apache.felix.webconsole.WebConsoleSecurityProvider3;

/**
 * OSGiConsoleSecurityProvider.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Service(WebConsoleSecurityProvider.class)
@Component(immediate = true)
public class OSGiConsoleSecurityProvider implements WebConsoleSecurityProvider3 {

	/**
	 * Role [OSGiAdmin] is already set by Undertow SecurityHandler.
	 */
	@Override
	public boolean authenticate(HttpServletRequest request, HttpServletResponse response) {
		return request.isUserInRole("OSGiAdmin");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object authenticate(String username, String password) {
		return "admin";
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
		// Set the status to [302] and location header to [/admin/logout] so that browser could redirect there.
		// ProxyDispatcherServlet will take care of Session invalidation later.
		response.setStatus(HttpServletResponse.SC_FOUND); 
		response.setHeader("Location", "/admin/logout");
	}

}
