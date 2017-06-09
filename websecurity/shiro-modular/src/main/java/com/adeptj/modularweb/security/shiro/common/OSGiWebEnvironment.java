package com.adeptj.modularweb.security.shiro.common;

import javax.servlet.ServletContext;

import org.apache.shiro.ShiroException;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.Destroyable;
import org.apache.shiro.util.Initializable;
import org.apache.shiro.web.env.MutableWebEnvironment;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;

public class OSGiWebEnvironment implements MutableWebEnvironment, Initializable, Destroyable {
	
	private ServletContext servletContext;

	@Override
	public FilterChainResolver getFilterChainResolver() {
		return null;
	}

	@Override
	public ServletContext getServletContext() {
		return this.servletContext;
	}

	@Override
	public WebSecurityManager getWebSecurityManager() {
		return null;
	}

	@Override
	public SecurityManager getSecurityManager() {
		return null;
	}

	@Override
	public void setFilterChainResolver(FilterChainResolver filterChainResolver) {
		
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Override
	public void setWebSecurityManager(WebSecurityManager webSecurityManager) {
		
	}
	
	@Override
	public void init() throws ShiroException {
		
	}
	
	@Override
	public void destroy() throws Exception {
		
	}

}
