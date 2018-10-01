/*
###############################################################################
#                                                                             # 
#    Copyright 2016, AdeptJ (http://www.adeptj.com)                           #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/

package com.adeptj.modules.mvc;

import org.apache.commons.io.IOUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;

import java.lang.invoke.MethodHandles;

import static javax.servlet.RequestDispatcher.ERROR_EXCEPTION;
import static org.osgi.service.component.annotations.ServiceScope.BUNDLE;

/**
 * Renders Html Templates using Trimou {@link MustacheEngine}
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(scope = BUNDLE)
public class DefaultTemplateEngine implements TemplateEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final int SB_CAPACITY = Integer.getInteger("template.builder.capacity", 100);

    @Reference
    private MustacheEngine mustacheEngine;

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(TemplateContext context) {
        try {
            Mustache mustache = this.mustacheEngine.getMustache(context.getTemplate());
            StringBuilder output = new StringBuilder(SB_CAPACITY);
            mustache.render(output, context.getContextObject());
            IOUtils.write(output.toString(), context.getResponse().getWriter());
        } catch (Exception ex) { // NOSONAR
            LOGGER.error(ex.getMessage(), ex);
            context.getRequest().setAttribute(ERROR_EXCEPTION, ex);
            throw new RenderException(ex.getMessage(), ex);
        }
    }
}