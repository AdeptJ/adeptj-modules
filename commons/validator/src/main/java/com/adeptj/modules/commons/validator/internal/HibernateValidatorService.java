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

import com.adeptj.modules.commons.validator.ValidatorService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.BootstrapConfiguration;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.executable.ExecutableType;
import java.lang.invoke.MethodHandles;
import java.util.Set;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * {@link HibernateValidator} based ValidatorService.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(service = ValidatorService.class)
public class HibernateValidatorService implements ValidatorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String VALIDATOR_FACTORY_INIT_MSG = "HibernateValidator initialized in [{}] ms!!";

    private static final String VALIDATOR_FACTORY_NULL_MSG = "javax.validation.ValidatorFactory is null!!";

    private final ValidatorFactory validatorFactory;

    private final boolean executableValidationEnabled;

    private final Set<ExecutableType> defaultValidatedExecutableTypes;

    public HibernateValidatorService() {
        try {
            long startTime = System.nanoTime();
            HibernateValidatorConfiguration validatorConfiguration = Validation.byProvider(HibernateValidator.class)
                    .configure();
            BootstrapConfiguration bootstrapConfiguration = validatorConfiguration.getBootstrapConfiguration();
            this.executableValidationEnabled = bootstrapConfiguration.isExecutableValidationEnabled();
            this.defaultValidatedExecutableTypes = bootstrapConfiguration.getDefaultValidatedExecutableTypes();
            this.validatorFactory = validatorConfiguration.buildValidatorFactory();
            Validate.validState((this.validatorFactory != null), VALIDATOR_FACTORY_NULL_MSG);
            LOGGER.info(VALIDATOR_FACTORY_INIT_MSG, NANOSECONDS.toMillis(System.nanoTime() - startTime));
        } catch (ValidationException | IllegalStateException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> void validate(T instance) {
        Validate.notNull(instance, "Object to be validated can't be null!!");
        Set<ConstraintViolation<T>> violations = this.getValidator().validate(instance);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Set<ConstraintViolation<T>> validateProperty(T instance, String property) {
        Validate.notNull(instance, "Object to be validated can't be null!!");
        Validate.isTrue(StringUtils.isNotEmpty(property), "property [%s] can't be blank!!", property);
        return this.getValidator().validateProperty(instance, property);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Set<ConstraintViolation<T>> getConstraintViolations(T instance) {
        Validate.notNull(instance, "Object to be validated can't be null!!");
        return this.getValidator().validate(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidatorFactory getValidatorFactory() {
        return new ValidatorFactoryWrapper(this.validatorFactory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Validator getValidator() {
        return this.validatorFactory.getValidator();
    }

    @Override
    public boolean isExecutableValidationEnabled() {
        return this.executableValidationEnabled;
    }

    @Override
    public Set<ExecutableType> getDefaultValidatedExecutableTypes() {
        return this.defaultValidatedExecutableTypes;
    }

    // <<------------------------------------------ OSGi INTERNAL ---------------------------------------------->>

    @Deactivate
    protected void stop() {
        this.validatorFactory.close();
    }
}
