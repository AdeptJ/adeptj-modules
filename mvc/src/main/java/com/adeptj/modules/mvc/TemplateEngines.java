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

import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.locator.ClassPathTemplateLocator;
import org.trimou.engine.locator.TemplateLocator;
import org.trimou.handlebars.i18n.ResourceBundleHelper;

import static org.trimou.engine.config.EngineConfigurationKey.DEFAULT_FILE_ENCODING;
import static org.trimou.engine.config.EngineConfigurationKey.END_DELIMITER;
import static org.trimou.engine.config.EngineConfigurationKey.START_DELIMITER;
import static org.trimou.engine.config.EngineConfigurationKey.TEMPLATE_CACHE_ENABLED;
import static org.trimou.engine.config.EngineConfigurationKey.TEMPLATE_CACHE_EXPIRATION_TIMEOUT;
import static org.trimou.handlebars.i18n.ResourceBundleHelper.Format.MESSAGE;

/**
 * Utility methods for Trimou {@link MustacheEngine}
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public final class TemplateEngines {

    private static final String RB_HELPER_NAME = "msg";

    // static utility methods only
    private TemplateEngines() {
    }

    static MustacheEngine newMustacheEngine() {
        long startTime = System.nanoTime();
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .registerHelper(RB_HELPER_NAME, new ResourceBundleHelper("tools/WEB-INF/i18n/messages", MESSAGE))
                .addTemplateLocator(newTemplateLocator())
                .setProperty(START_DELIMITER, "{{")
                .setProperty(END_DELIMITER, "}}")
                .setProperty(DEFAULT_FILE_ENCODING, "UTF-8")
                .setProperty(TEMPLATE_CACHE_ENABLED, false)
                .setProperty(TEMPLATE_CACHE_EXPIRATION_TIMEOUT, 0)
                .build();
        return engine;
    }

    private static TemplateLocator newTemplateLocator() {
        return ClassPathTemplateLocator.builder()
                .setPriority(1)
                .setRootPath("")
                .setSuffix("")
                .setScanClasspath(false)
                .setClassLoader(TemplateEngines.class.getClassLoader())
                .build();
    }
}
