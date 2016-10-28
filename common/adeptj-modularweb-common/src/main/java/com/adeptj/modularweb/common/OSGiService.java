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
 * OSGiService Annotation, Fields annotated with this will be injected with the required
 * OSGi service, e.g fields in JAX-RS resources.
 * 
 * @author Rakesh.Kumar, AdeptJ
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface OSGiService {

	/**
	 * specifies the RFC 1960-based filter string, which is evaluated when
	 * retrieving the service. If empty string or left out, then no filtering is
	 * being performed.
	 * 
	 * @see "Core Specification, section 5.5, for a description of the filter string"
	 * @see <a href="http://www.ietf.org/rfc/rfc1960.txt">RFC 1960</a>
	 */
	public String filter() default "";
}
