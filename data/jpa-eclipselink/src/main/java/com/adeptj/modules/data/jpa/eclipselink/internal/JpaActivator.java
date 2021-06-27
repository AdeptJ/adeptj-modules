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
package com.adeptj.modules.data.jpa.eclipselink.internal;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.osgi.annotation.bundle.Header;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

import static org.osgi.framework.Constants.BUNDLE_ACTIVATOR;

/**
 * JPA Module {@link BundleActivator}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Header(name = BUNDLE_ACTIVATOR, value = "${@class}")
public class JpaActivator implements BundleActivator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String FIELD_DEFAULT_LOG = "defaultLog";

    @Override
    public void start(BundleContext context) {
        // Nothing to do here!!
    }

    @Override
    public void stop(BundleContext context) {
        disposeEclipseLinkSingletonLog();
    }

    static void disposeEclipseLinkSingletonLog() {
        // We have no other way to set the defaultLog field to null, this will prevent a potential memory leak because
        // AbstractSessionLog is still holding the SLF4JLogger instance as a static field and there is no need
        // of SLF4JLogger if this bundle or EntityManagerFactoryLifecycle is going to be stopped.
        try {
            Field field = FieldUtils.getDeclaredField(AbstractSessionLog.class, FIELD_DEFAULT_LOG, true);
            Object defaultLog = field.get(null);
            if (defaultLog instanceof SessionLog) {
                ((SessionLog) defaultLog).setSession(null);
                field.set(null, null);
            }
        } catch (IllegalAccessException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
