package com.adeptj.modules.mvc;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardFilterAsyncSupported;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletName;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletPattern;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@HttpWhiteboardServletName("AdeptJ ViewEngineServlet")
@HttpWhiteboardServletPattern("/view-engine/*")
@HttpWhiteboardFilterAsyncSupported
@Component(service = Servlet.class, scope = PROTOTYPE)
public class ViewEngineServlet extends HttpServlet {

    @Reference
    private TemplateEngine templateEngine;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ClassLoader old = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            this.templateEngine.render(TemplateContext.builder()
                    .request(req)
                    .response(resp)
                    .template("test")
                    .build());
        } finally {
            Thread.currentThread().setContextClassLoader(old);
        }
    }
}
