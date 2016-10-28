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
package com.adeptj.modularweb.beanval.internal.activator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * BeanValidationActivator
 * 
 * @author Rakesh.Kumar, AdeptJ
 */
public class BeanValidationActivator implements BundleActivator {

	// private ValidatorFactory validatorFactory;

	// private ServiceRegistration<ValidatorService> servRegValidatorService;

	@Override
	public void start(BundleContext context) throws Exception {
		/*Configuration<?> config = Validation.byDefaultProvider().providerResolver(new OSGiValidationProviderResolver())
				.configure();
        config.parameterNameProvider(new ReflectionParameterNameProvider());
		config.messageInterpolator(new LocaleSpecificMessageInterpolator(config.getDefaultMessageInterpolator()));
		validatorFactory = config.buildValidatorFactory();
		Dictionary<String, Object> props = new Hashtable<>();
		props.put(Constants.SERVICE_VENDOR, "AdeptJ");
		props.put(Constants.SERVICE_DESCRIPTION, "AdeptJ ValidatorService");
		servRegValidatorService = context.registerService(ValidatorService.class,
				new OSGiBeanValidatorService(this.validatorFactory.getValidator(), config.getBootstrapConfiguration()),
				props);*/
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// validatorFactory.close();
		// servRegValidatorService.unregister();
	}
}
