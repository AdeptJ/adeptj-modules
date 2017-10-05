/* 
 * =============================================================================
 * 
 * Copyright (c) 2016 AdeptJ
 * Copyright (c) 2016 Rakesh Kumar <irakeshk@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * =============================================================================
 */
package com.adeptj.modules.commons.bv.service.internal;

import com.adeptj.modules.commons.bv.service.ValidatorService;

import javax.validation.BootstrapConfiguration;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

/**
 * ValidatorService Implementation.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class OSGiBeanValidatorService implements ValidatorService {

	private volatile Validator validator;
	
	private BootstrapConfiguration bootstrapConfig;

	public OSGiBeanValidatorService(Validator validator, BootstrapConfiguration bootstrapConfig) {
		this.validator = validator;
		this.bootstrapConfig = bootstrapConfig;
	}

	@Override
	public <T> void validate(T objectToValidate) {
		Set<ConstraintViolation<T>> violations = this.validator.validate(objectToValidate);
		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
	}

	@Override
	public Validator getValidator() {
		return validator;
	}

	@Override
	public BootstrapConfiguration getBootstrapConfiguration() {
		return bootstrapConfig;
	}
}
