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
package com.adeptj.modularweb.common;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * ViewEngineType Annotation, Fields annotated with this will be injected with the required
 * ViewEngine service, e.g fields in JAX-RS resources.
 * 
 * @author Rakesh.Kumar, AdeptJ
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface ViewEngineType {

	public Type type() default Type.NONE;
	
	public String extn() default "html";
	
	public String name() default "";
	
	public enum Type {
		
		THYMELEAF,
		
		JSP,
		
		FREEMARKER,
		
		VELOCITY,
		
		NONE
	}
}
