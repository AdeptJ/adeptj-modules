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
package com.adeptj.modules.jaxrs.resteasy.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JaxrsResourceCollector.
 * 
 * @author Rakesh.Kumar, AdeptJ.
 */
public enum JaxrsResourceCollector {

	INSTANCE;
	
	private Map<String, Object> resources;
	
	JaxrsResourceCollector() {
		resources = new ConcurrentHashMap<>(32);
	}
	
	public Map<String, Object> getResources() {
		return resources;
	}
	
	public void addResource(String alias, Object resType) {
		resources.put(alias, resType);
	}
	
	public void clearResources() {
		resources.clear();;
	}
}
