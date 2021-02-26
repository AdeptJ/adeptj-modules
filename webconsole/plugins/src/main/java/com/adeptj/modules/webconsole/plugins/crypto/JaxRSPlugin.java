package com.adeptj.modules.webconsole.plugins.crypto;

import com.adeptj.modules.commons.utils.annotation.WebConsolePlugin;
import org.apache.felix.webconsole.AbstractWebConsolePlugin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.DynamicFeature;
import java.io.IOException;
import java.io.PrintWriter;

@WebConsolePlugin(label = "jaxrs", title = "JaxRS")
@Component(immediate = true, service = Servlet.class, property = "felix.webconsole.configprinter.modes=always")
public class JaxRSPlugin extends AbstractWebConsolePlugin {

    @Override
    protected void renderContent(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.getWriter().println("Here is the JaxRS information.");
    }

    @Override
    public String getLabel() {
        return "jaxrs";
    }

    @Override
    public String getTitle() {
        return "JaxRS";
    }

    // Called by WebConsole
    public void printConfiguration(final PrintWriter pw) {
        pw.println("JaxRS Details:");
        pw.println();
        pw.println("Registered Provider Classes");
        pw.println();
        ResteasyProviderFactory providerFactory = ResteasyProviderFactory.getInstance();
        for (Class<?> providerClass : providerFactory.getProviderClasses()) {
            pw.printf("%s \n", providerClass.getName());
        }
        pw.println();
        pw.println("Registered Provider Instances");
        pw.println();
        for (Object provider : providerFactory.getProviderInstances()) {
            pw.printf("%s \n", provider);
        }
        pw.println();
        pw.println("Registered Dynamic Features");
        pw.println();
        for (DynamicFeature dynamicFeature : providerFactory.getServerDynamicFeatures()) {
            pw.printf("%s \n", dynamicFeature);
        }
    }
}
