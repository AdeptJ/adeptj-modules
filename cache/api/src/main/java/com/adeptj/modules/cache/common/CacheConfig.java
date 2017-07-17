/*
###############################################################################
#                                                                             # 
#    Copyright 2016, AdeptJ (http://www.adeptj.com)                           #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/
package com.adeptj.modules.cache.common;

import java.util.HashMap;
import java.util.Map;

/**
 * CacheConfig.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class CacheConfig {

    private String cacheName;

    private String cacheServicePid;

    private Object ttlSeconds;

    private Object cacheEntries;

    private Map<String, ?> otherConfigs;

    public CacheConfig(String cacheName, String cacheServicePid, long ttlSeconds, long cacheEntries) {
        this.cacheName = cacheName;
        this.cacheServicePid = cacheServicePid;
        this.ttlSeconds = ttlSeconds;
        this.cacheEntries = cacheEntries;
        this.otherConfigs = new HashMap<>();
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

    public Object getTtlSeconds() {
        return ttlSeconds;
    }

    public void setTtlSeconds(Object ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }

    public Object getCacheEntries() {
        return cacheEntries;
    }

    public void setCacheEntries(Object cacheEntries) {
        this.cacheEntries = cacheEntries;
    }

    public Map<String, ?> getOtherConfigs() {
        return otherConfigs;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cacheEntries == null) ? 0 : cacheEntries.hashCode());
        result = prime * result + ((cacheName == null) ? 0 : cacheName.hashCode());
        result = prime * result + ((cacheServicePid == null) ? 0 : cacheServicePid.hashCode());
        result = prime * result + ((ttlSeconds == null) ? 0 : ttlSeconds.hashCode());
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
        if (cacheEntries == null) {
            if (other.cacheEntries != null)
                return false;
        } else if (!cacheEntries.equals(other.cacheEntries))
            return false;
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
        if (ttlSeconds == null) {
            if (other.ttlSeconds != null)
                return false;
        } else if (!ttlSeconds.equals(other.ttlSeconds))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CacheConfig [cacheName=" + cacheName + ", cacheServicePid=" + cacheServicePid + ", ttlSeconds="
                + ttlSeconds + "]";
    }
}