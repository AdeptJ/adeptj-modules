package com.adeptj.modules.mvc;

import com.adeptj.modules.viewengine.api.ViewEngine;
import com.adeptj.modules.viewengine.core.ViewEngineContext;
import com.adeptj.modules.viewengine.core.ViewEngineException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * ThymeleafViewEngine
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(
        service = ViewEngine.class,
        scope = ServiceScope.BUNDLE
)
public class ThymeleafViewEngine implements ViewEngine {

    @Override
    public boolean supports(String view) {
        return true;
    }

    @Override
    public void processView(ViewEngineContext engineContext) throws ViewEngineException {

    }

    @Activate
    protected void activate(ComponentContext componentContext) {
        BundleTemplateResolver templateResolver = new BundleTemplateResolver(componentContext.getUsingBundle());
        templateResolver.setPrefix("views/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        // default is cacheable
        templateResolver.setCacheable(false);
        templateResolver.setOrder(1);
        // RK: This is performance intensive OP, need to look into code for a
        // better alternative
        templateResolver.setCheckExistence(true);
        TemplateEngine engine = new TemplateEngine();
        engine.addTemplateResolver(templateResolver);
    }
}
