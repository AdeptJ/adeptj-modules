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
package com.adeptj.modularweb.security.shiro.common;

import java.util.Collection;
import java.util.Collections;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.RolePermissionResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RolePermissionResolverImpl implements RolePermissionResolver {

	private static final Logger LOGGER = LoggerFactory.getLogger(RolePermissionResolverImpl.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<Permission> resolvePermissionsInRole(String role) {
		LOGGER.info("Resolving permissions in role: [{}]", role);
		return Collections.emptySet();
	}

}
