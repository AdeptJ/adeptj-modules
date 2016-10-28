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

import com.adeptj.modularweb.viewengine.core.ViewEngineContext;
import com.adeptj.modularweb.viewengine.core.ViewEngineException;

/**
 * ViewEngine.
 * 
 * @author Rakesh.Kumar, AdeptJ.
 */
public interface ViewEngine {

	String DEFAULT_VIEW_FOLDER = "/views";

	/**
	 * Returns <code>true</code> if this engine can process the view or
	 * <code>false</code> otherwise.
	 *
	 * @param view
	 *            the view.
	 * @return outcome of supports test.
	 */
	boolean supports(String view);

	/**
	 * <p>
	 * Process a view given a
	 * {@link com.adeptj.modularweb.viewengine.core.ViewEngineContext}. Processing a
	 * view involves <i>merging</i> the model and template data and writing the
	 * result to an output stream.
	 * </p>
	 *
	 * <p>
	 * Following the Java EE threading model, the underlying view engine
	 * implementation must support this method being called by different
	 * threads. Any resources allocated during view processing must be released
	 * before the method returns.
	 * </p>
	 *
	 * @param context
	 *            the context needed for processing.
	 * @throws ViewEngineException
	 *             if an error occurs during processing.
	 */
	void processView(ViewEngineContext engineContext) throws ViewEngineException;
	
}