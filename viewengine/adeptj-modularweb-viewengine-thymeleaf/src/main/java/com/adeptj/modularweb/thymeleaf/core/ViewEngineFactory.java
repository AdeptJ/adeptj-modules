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
package com.adeptj.modularweb.thymeleaf.core;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;

import com.adeptj.modularweb.thymeleaf.engine.impl.ThymeleafViewEngine;
import com.adeptj.modularweb.viewengine.api.ViewEngine;

/**
 * ViewEngineFactory.
 * 
 * @author Rakesh.Kumar, AdeptJ.
 */
public class ViewEngineFactory implements ServiceFactory<ViewEngine> {


    private static final Logger LOGGER = LoggerFactory.getLogger(ViewEngineFactory.class);

    /**
     * Per Bundle TemplateEngine.
     *
     * @param bundle
     * @param registration
     * @return TemplateEngine
     */
	@Override
	public ViewEngine getService(Bundle bundle, ServiceRegistration<ViewEngine> registration) {
        LOGGER.info("Providing ViewEngine for Bundle: [{}]", bundle);
		BundleTemplateResolver templateResolver = new BundleTemplateResolver(bundle);
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
		return new ThymeleafViewEngine(engine);
	}

	@Override
	public void ungetService(Bundle bundle, ServiceRegistration<ViewEngine> registration, ViewEngine engine) {
        LOGGER.info("unget ViewEngine for Bundle: [{}]", bundle);
	}

}
