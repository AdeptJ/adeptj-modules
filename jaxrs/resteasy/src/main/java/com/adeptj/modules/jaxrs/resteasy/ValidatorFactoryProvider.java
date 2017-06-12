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
package com.adeptj.modules.jaxrs.resteasy;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.parameternameprovider.ReflectionParameterNameProvider;
import org.slf4j.LoggerFactory;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

/**
 * ValidatorFactoryProvider, initializes the HibernateValidatorFactory.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public enum ValidatorFactoryProvider {

    INSTANCE;

    public ValidatorFactory getValidatorFactory() {
        HibernateValidatorConfiguration config = Validation.byProvider(HibernateValidator.class).configure();
        // ValidatorFactory Provided by RESTEasy does not give the parameter names of validated fields.
        config.parameterNameProvider(new ReflectionParameterNameProvider());
        LoggerFactory.getLogger(ValidatorFactoryProvider.class).info("Hibernate Validator Initialized!!");
        return config.buildValidatorFactory();
    }
}
