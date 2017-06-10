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
package com.adeptj.modules.viewengine.core;

import com.adeptj.modules.viewengine.api.ViewEngine;

import java.util.Locale;

/**
 * Viewable.
 * 
 * @author Rakesh.Kumar, AdeptJ.
 */
public class Viewable {

	private final String view;

	private final Locale locale;

	private final Models models;

	private final ViewEngine viewEngine;

	public Viewable(String view, Locale locale, Models models, ViewEngine viewEngine) {
		this.view = view;
		this.locale = locale;
		this.models = models;
		this.viewEngine = viewEngine;
	}

	public String getView() {
		return view;
	}

	public Locale getLocale() {
		return locale;
	}

	public Models getModels() {
		return models;
	}

	public ViewEngine getViewEngine() {
		return viewEngine;
	}
}
