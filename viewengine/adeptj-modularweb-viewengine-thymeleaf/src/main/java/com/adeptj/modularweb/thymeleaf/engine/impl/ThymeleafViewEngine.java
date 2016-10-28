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
package com.adeptj.modularweb.thymeleaf.engine.impl;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import com.adeptj.modularweb.viewengine.api.ViewEngine;
import com.adeptj.modularweb.viewengine.core.ViewEngineContext;
import com.adeptj.modularweb.viewengine.core.ViewEngineException;

/**
 * ThymeleafViewEngine.
 * 
 * @author Rakesh.Kumar, AdeptJ.
 */
public class ThymeleafViewEngine implements ViewEngine {

	private static final Logger LOGGER = LoggerFactory.getLogger(ThymeleafViewEngine.class);

	private TemplateEngine templateEngine;

	public ThymeleafViewEngine(TemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}
	
	@Override
	public boolean supports(String view) {
		return true;
	}

	@Override
	public void processView(ViewEngineContext engineContext) throws ViewEngineException {
		String view = engineContext.getView();
		LOGGER.info("Processing view:[{}]", view);
		try {
			HttpServletRequest request = engineContext.getRequest();
			HttpServletResponse response = engineContext.getResponse();
			this.templateEngine.process(view, new WebContext(request, response, request.getServletContext(),
					engineContext.getLocale(), engineContext.getModels()), response.getWriter());
		} catch (IOException ex) {
			LOGGER.error("IOException while processing view!!", ex);
			throw new ViewEngineException(ex.getMessage(), ex);
		}
	}

}
