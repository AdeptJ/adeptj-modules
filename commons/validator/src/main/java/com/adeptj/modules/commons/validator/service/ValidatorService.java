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

import javax.validation.ConstraintViolation;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * Service interface for validating Java beans, JAX-RS resources etc.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
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
     * Validates the given instance.
     *
     * @param instance the object to be validated.
     * @param <T>      type of the object to be validated
     * @return set of ConstraintViolation
     * @throws javax.validation.ConstraintViolationException when object being validated failed validation.
     */
    <T> Set<ConstraintViolation<T>> getConstraintViolations(T instance);

    /**
     * Returns the ValidatorFactory instance.
     *
     * @return the ValidatorFactory instance.
     */
    ValidatorFactory getValidatorFactory();
}
