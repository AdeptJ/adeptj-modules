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
package com.adeptj.modularweb.log.factory;

import java.util.Dictionary;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

public class LoggingConfigFactory implements ManagedServiceFactory {

	private final ConcurrentMap<String, LoggingConfig> configs = new ConcurrentHashMap<>();

	public LoggingConfigFactory() {
		
	}

	@Override
	public String getName() {
		return "AdeptJ ModularWeb LoggingConfigFactory";
	}

	@Override
	public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {
		LoggingConfig.Builder builder = new LoggingConfig.Builder();
		builder.writerType((String) properties.get("writer.type"));
		LoggingConfig config = builder.build();
		configs.put(pid, config);
	}

	@Override
	public void deleted(String pid) {

	}

}
