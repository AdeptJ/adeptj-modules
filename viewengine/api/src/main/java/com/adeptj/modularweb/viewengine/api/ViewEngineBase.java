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
package com.adeptj.modularweb.viewengine.api;

import com.adeptj.modularweb.viewengine.core.PathUtils;
import com.adeptj.modularweb.viewengine.core.ViewEngineContext;

/**
 * Base class for view engines that factors out all common logic.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public abstract class ViewEngineBase implements ViewEngine {

	/**
	 * Resolves a view path If the view is absolute, starts with '/', then it is
	 * returned unchanged.
	 *
	 * @param context
	 *            view engine context.
	 * @return resolved view.
	 */
	protected String resolveView(ViewEngineContext context) {
		final String view = context.getView();
		if (PathUtils.hasStartingSlash(view)) {
			return view;
		}
		return view;
	}
}
