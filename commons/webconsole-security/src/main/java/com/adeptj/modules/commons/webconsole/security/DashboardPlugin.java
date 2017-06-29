package com.adeptj.modules.commons.webconsole.security;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.webconsole.SimpleWebConsolePlugin;
import org.apache.felix.webconsole.WebConsoleConstants;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Service(value = Servlet.class)
@Properties({ @Property(name = WebConsoleConstants.PLUGIN_LABEL, value = "tools"),
		@Property(name = WebConsoleConstants.PLUGIN_TITLE, value = "AdeptJ Tools") })
public class DashboardPlugin extends SimpleWebConsolePlugin {

	private static final long serialVersionUID = 8041033223220201144L;

	public DashboardPlugin() {
		super("tools", "AdeptJ Tools", "Main", (String[]) null);
	}

	@Override
	protected void renderContent(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.sendRedirect("/tools/dashboard");
	}

}
