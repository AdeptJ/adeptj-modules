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

package com.adeptj.modules.commons.validator.service;

import org.osgi.annotation.versioning.ProviderType;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * Service interface for validating Java beans, JAX-RS resources etc.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ProviderType
public interface ValidatorService {

    /**
     * Validates the given instance.
     *
     * @param instance the object to be validated.
     * @param <T>      type of the object to be validated
     * @throws javax.validation.ConstraintViolationException when object being validated failed validation.
     */
    <T> void validate(T instance);

    /**
     * Validates the given instance property.
     *
     * @param instance the object to be validated.
     * @param property the property of the instance passed which is to be validated.
     * @param <T>      type of the object to be validated
     * @return set of ConstraintViolation
     * @throws javax.validation.ConstraintViolationException when object being validated failed validation.
     * @since 1.0.2.Final
     */
    <T> Set<ConstraintViolation<T>> validateProperty(T instance, String property);

    /**
     * Validates the given instance.
     *
     * @param instance the object to be validated.
     * @param <T>      type of the object to be validated
     * @return set of ConstraintViolation
     * @throws javax.validation.ConstraintViolationException when object being validated failed validation.
     * @since 1.0.1.Final
     */
    <T> Set<ConstraintViolation<T>> getConstraintViolations(T instance);

    /**
     * Returns the {@link ValidatorFactory} instance.
     *
     * @return the {@link ValidatorFactory} instance.
     */
    ValidatorFactory getValidatorFactory();

    /**
     * Returns the {@link Validator} instance.
     *
     * @return the {@link Validator} instance.
     */
    Validator getValidator();
}
