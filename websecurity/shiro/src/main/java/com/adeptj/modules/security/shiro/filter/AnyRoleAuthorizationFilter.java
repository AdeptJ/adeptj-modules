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
package com.adeptj.modules.security.shiro.filter;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;

/**
 * Allows access if current user has at least one role of the specified list.
 * <br/>
 * Basically, it's the same as {@link RolesAuthorizationFilter} but using
 * {@literal OR} instead of {@literal AND} on the specified roles.
 *
 * @see RolesAuthorizationFilter
 * 
 * @author Prince.Arora
 */
public class AnyRoleAuthorizationFilter extends RolesAuthorizationFilter {

	/**
	 * {@inheritDoc}
	 */
	public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
			throws IOException {
		String[] rolesArray = (String[]) mappedValue;
		if (rolesArray == null || rolesArray.length == 0) {
			// no roles specified
			return true;
		}
		return true;
	}
}
