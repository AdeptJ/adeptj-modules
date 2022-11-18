package com.adeptj.modules.graphql;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletName;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletPattern;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import static graphql.kickstart.servlet.HttpRequestHandler.STATUS_BAD_REQUEST;

@HttpWhiteboardServletName("AdeptJ GraphQL Proxy Servlet")
@HttpWhiteboardServletPattern("/graphql")
@Component(service = Servlet.class)
public class GraphQLProxyServlet extends HttpServlet {

    private HttpServlet graphqlServlet;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if ("GET".equals(method) || "POST".equals(method)) {
            this.graphqlServlet.service(req, resp);
            return;
        }
        resp.setStatus(STATUS_BAD_REQUEST);
    }

    @Reference(service = HttpServlet.class, target = "(alias=/graphql)")
    public void setGraphqlServlet(HttpServlet graphqlServlet) {
        this.graphqlServlet = graphqlServlet;
    }
}
