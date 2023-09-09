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

package com.adeptj.modules.commons.validator.internal;

import jakarta.validation.ClockProvider;
import jakarta.validation.ConstraintValidatorFactory;
import jakarta.validation.MessageInterpolator;
import jakarta.validation.ParameterNameProvider;
import jakarta.validation.TraversableResolver;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorContext;
import jakarta.validation.ValidatorFactory;

/**
 * Wrapper around {@link ValidatorFactory} to prevent close method call by consumer code.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
class ValidatorFactoryWrapper implements ValidatorFactory {

    private final ValidatorFactory delegate;

    ValidatorFactoryWrapper(ValidatorFactory delegate) {
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Validator getValidator() {
        return this.delegate.getValidator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidatorContext usingContext() {
        return this.delegate.usingContext();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageInterpolator getMessageInterpolator() {
        return this.delegate.getMessageInterpolator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TraversableResolver getTraversableResolver() {
        return this.delegate.getTraversableResolver();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConstraintValidatorFactory getConstraintValidatorFactory() {
        return this.delegate.getConstraintValidatorFactory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ParameterNameProvider getParameterNameProvider() {
        return this.delegate.getParameterNameProvider();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClockProvider getClockProvider() {
        return this.delegate.getClockProvider();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T unwrap(Class<T> type) {
        return this.delegate.unwrap(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        throw new UnsupportedOperationException("Caller is not supposed to close ValidatorFactory!!");
    }
}
