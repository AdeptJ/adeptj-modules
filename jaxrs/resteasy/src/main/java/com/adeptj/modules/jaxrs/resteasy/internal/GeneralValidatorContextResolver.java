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
import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.of;
import static javax.validation.executable.ExecutableType.ALL;
import static javax.validation.executable.ExecutableType.IMPLICIT;
import static javax.validation.executable.ExecutableType.NONE;

/**
 * GeneralValidatorContextResolver used to provide the ValidatorFactory that gives the parameter names of validated fields.
 * <p>
 * RESTEasy does not provide that.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Provider
public class GeneralValidatorContextResolver implements ContextResolver<GeneralValidator> {

    private volatile GeneralValidator validator;

    @Override
    public GeneralValidator getContext(Class<?> type) {
        if (this.validator == null) {
            try {
                this.validator = new GeneralValidatorImpl(ValidatorFactoryProvider.INSTANCE.getValidatorFactory(),
                        true, unmodifiableSet(complementOf(of(ALL, NONE, IMPLICIT))));
            } catch (Exception ex) {
                throw new ValidationException(Messages.MESSAGES.unableToLoadValidationSupport(), ex);
            }
        }
        return this.validator;
    }
}
