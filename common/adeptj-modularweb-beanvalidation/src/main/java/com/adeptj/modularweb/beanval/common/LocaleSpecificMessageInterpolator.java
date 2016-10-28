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
package com.adeptj.modularweb.beanval.common;

import java.util.Locale;

import javax.validation.MessageInterpolator;

/**
 * Delegates to a MessageInterpolator implementation but enforces a given Locale.
 * 
 * @author Rakesh.Kumar, AdeptJ
 */
public class LocaleSpecificMessageInterpolator implements MessageInterpolator {

	private final MessageInterpolator defaultInterpolator;

	public LocaleSpecificMessageInterpolator(MessageInterpolator interpolator) {
		this.defaultInterpolator = interpolator;
	}

	@Override
	public String interpolate(String message, Context context) {
		return defaultInterpolator.interpolate(message, context, Locales.get());
	}

	@Override
	public String interpolate(String message, Context context, Locale locale) {
		return defaultInterpolator.interpolate(message, context, locale);
	}
}