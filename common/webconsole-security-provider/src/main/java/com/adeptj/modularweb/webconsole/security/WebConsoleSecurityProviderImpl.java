package com.adeptj.modularweb.webconsole.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.webconsole.WebConsoleSecurityProvider;
import org.apache.felix.webconsole.WebConsoleSecurityProvider3;

@Service(WebConsoleSecurityProvider.class)
@Component(immediate = true)
public class WebConsoleSecurityProviderImpl implements WebConsoleSecurityProvider3 {

	@Override
	public boolean authenticate(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("WebConsoleSecurityProviderImpl.authenticate");
		return true;
	}

	@Override
	public Object authenticate(String username, String password) {
		System.out.println("WebConsoleSecurityProviderImpl.authenticate2");
		return "admin";
	}

	@Override
	public boolean authorize(Object user, String role) {
		System.out.println("WebConsoleSecurityProviderImpl.authorize");
		return true;
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("WebConsoleSecurityProviderImpl.logout");
	}

}
