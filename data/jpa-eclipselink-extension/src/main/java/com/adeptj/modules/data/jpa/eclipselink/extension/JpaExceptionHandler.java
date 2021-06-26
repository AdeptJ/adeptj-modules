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

package com.adeptj.modules.data.jpa.eclipselink.extension;

import org.eclipse.persistence.exceptions.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * Use eclipselink.exception-handler to specify the EclipseLink exception handler class: a Java class that implements
 * the {@link ExceptionHandler} interface and provides a default (zero-argument) constructor.
 * <p>
 * See more at: http://eclipse.org/eclipselink/documentation/2.5/jpa/extensions/p_exception_handler.htm
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class JpaExceptionHandler implements ExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String SYS_PROP_ENABLE_EXCEPTION_HANDLER_LOGGING = "enable.eclipselink.exceptionhandler.logging";

    /**
     * {@inheritDoc}
     */
    @Override
    public Object handleException(RuntimeException exception) {
        if (Boolean.getBoolean(SYS_PROP_ENABLE_EXCEPTION_HANDLER_LOGGING)) {
            // Log it as of now, perform any other task on passed Exception if needed.
            LOGGER.error(exception.getMessage(), exception);
        }
        throw exception;
    }
}
