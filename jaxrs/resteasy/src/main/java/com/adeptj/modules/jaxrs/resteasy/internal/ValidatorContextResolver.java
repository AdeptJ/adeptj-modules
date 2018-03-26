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
package com.adeptj.modules.jaxrs.resteasy.internal;

import com.adeptj.modules.jaxrs.resteasy.ValidatorFactoryProvider;
import org.jboss.resteasy.plugins.validation.GeneralValidatorImpl;
import org.jboss.resteasy.plugins.validation.i18n.Messages;
import org.jboss.resteasy.spi.validation.GeneralValidator;

import javax.validation.ValidationException;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import static java.util.Collections.unmodifiableSet;
import static java.util.EnumSet.of;
import static javax.validation.executable.ExecutableType.CONSTRUCTORS;
import static javax.validation.executable.ExecutableType.NON_GETTER_METHODS;

/**
 * JAX-RS default ContextResolver for RESTEasy's {@link GeneralValidator}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Provider
public class ValidatorContextResolver implements ContextResolver<GeneralValidator> {

    private volatile GeneralValidator validator;

    ValidatorContextResolver() {
    }

    @Override
    public GeneralValidator getContext(Class<?> type) {
        if (this.validator == null) {
            try {
                this.validator = new GeneralValidatorImpl(ValidatorFactoryProvider.INSTANCE.getValidatorFactory(),
                        true,
                        unmodifiableSet(of(CONSTRUCTORS, NON_GETTER_METHODS)));
            } catch (Exception ex) { // NOSONAR
                throw new ValidationException(Messages.MESSAGES.unableToLoadValidationSupport(), ex);
            }
        }
        return this.validator;
    }

    public void setValidator(GeneralValidator validator) {
        this.validator = validator;
    }
}
