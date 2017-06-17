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
package com.adeptj.modules.security.shiro.listener;

import javax.servlet.ServletContextEvent;

import org.apache.shiro.web.env.EnvironmentLoaderListener;

import com.adeptj.modularweb.common.ClassLoaders;

/**
 * ExtEnvironmentLoaderListener.
 * 
 * @author Rakesh.Kumar, AdeptJ..
 */
public class ExtEnvironmentLoaderListener extends EnvironmentLoaderListener {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ClassLoaders.executeWith(ExtEnvironmentLoaderListener.class.getClassLoader(), (() -> {
			super.contextInitialized(sce);
			return null;
		}));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ClassLoaders.executeWith(ExtEnvironmentLoaderListener.class.getClassLoader(), (() -> {
			super.contextDestroyed(sce);
			return null;
		}));
	}

}
