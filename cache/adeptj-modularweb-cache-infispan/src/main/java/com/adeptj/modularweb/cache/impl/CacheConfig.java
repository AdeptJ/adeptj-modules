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
package com.adeptj.modularweb.cache.impl;

/**
 * CacheConfig.
 * 
 * @author Rakesh.Kumar, AdeptJ
 */
public class CacheConfig {

	private String cacheName;

	private String cacheServicePid;

	private long ttlSeconds;
	
	private long cacheEntries;

	public CacheConfig(String cacheName, String cacheServicePid, long ttlSeconds, long cacheEntries) {
		this.cacheName = cacheName;
		this.cacheServicePid = cacheServicePid;
		this.ttlSeconds = ttlSeconds;
		this.cacheEntries = cacheEntries;
	}

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public String getCacheServicePid() {
		return cacheServicePid;
	}

	public void setCacheServicePid(String cacheServicePid) {
		this.cacheServicePid = cacheServicePid;
	}

	public long getTtlSeconds() {
		return ttlSeconds;
	}

	public void setTtlSeconds(long ttlSeconds) {
		this.ttlSeconds = ttlSeconds;
	}

	public long getCacheEntries() {
		return cacheEntries;
	}

	public void setCacheEntries(long cacheEntries) {
		this.cacheEntries = cacheEntries;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cacheName == null) ? 0 : cacheName.hashCode());
		result = prime * result + ((cacheServicePid == null) ? 0 : cacheServicePid.hashCode());
		result = prime * result + (int) (ttlSeconds ^ (ttlSeconds >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CacheConfig other = (CacheConfig) obj;
		if (cacheName == null) {
			if (other.cacheName != null)
				return false;
		} else if (!cacheName.equals(other.cacheName))
			return false;
		if (cacheServicePid == null) {
			if (other.cacheServicePid != null)
				return false;
		} else if (!cacheServicePid.equals(other.cacheServicePid))
			return false;
		if (ttlSeconds != other.ttlSeconds)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CacheConfig [cacheName=" + cacheName + ", cacheServicePid=" + cacheServicePid + ", ttlSeconds="
				+ ttlSeconds + "]";
	}
}