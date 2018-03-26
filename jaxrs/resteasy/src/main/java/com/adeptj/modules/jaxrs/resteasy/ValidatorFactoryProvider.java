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

import com.adeptj.modules.commons.utils.Loggers;
import org.hibernate.validator.HibernateValidator;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;


/**
 * Initializes the HibernateValidator in case ValidatorService is unavailable.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public enum ValidatorFactoryProvider {

    INSTANCE;

    private volatile ValidatorFactory defaultValidatorFactory;

    private volatile ValidatorFactory serviceValidatorFactory;

    public ValidatorFactory getValidatorFactory() {
        if (this.serviceValidatorFactory == null) {
            if (this.defaultValidatorFactory == null) {
                this.defaultValidatorFactory = Validation.byProvider(HibernateValidator.class)
                        .configure()
                        .buildValidatorFactory();
                Loggers.get(this.getClass()).info("Default HibernateValidator Initialized!!");
            }
            return defaultValidatorFactory;
        }
        return this.serviceValidatorFactory;
    }

    // Will be set by JaxRSDispatcherServlet if ValidatorService is available.
    public void setServiceValidatorFactory(ValidatorFactory serviceValidatorFactory) { // NOSONAR
        this.serviceValidatorFactory = serviceValidatorFactory;
    }
}
