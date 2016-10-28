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
package com.adeptj.modularweb.oauth.common;

import com.github.scribejava.core.builder.api.DefaultApi20;

/**
 * OAuthProvider.
 * 
 * @author Rakesh.Kumar, AdeptJ
 */
public class OAuthProvider {

	private String providerName;

	private String apiKey;

	private String apiSecret;

	private String callbackURL;
	
	private DefaultApi20 api;

	private OAuthProvider(String providerName, String apiKey, String apiSecret, String callbackURL, DefaultApi20 api) {
		this.providerName = providerName;
		this.apiKey = apiKey;
		this.apiSecret = apiSecret;
		this.callbackURL = callbackURL;
		this.api = api;
	}

	public String getProviderName() {
		return providerName;
	}

	public String getApiKey() {
		return apiKey;
	}

	public String getApiSecret() {
		return apiSecret;
	}

	public String getCallbackURL() {
		return callbackURL;
	}
	
	public DefaultApi20 getApi() {
		return api;
	}

	/**
	 * OAuthProvider.Builder
	 * 
	 * @author Rakesh.Kumar, AdeptJ
	 */
	public static class Builder {

		private String providerName;

		private String apiKey;

		private String apiSecret;

		private String callbackURL;
		
		private DefaultApi20 api;

		public Builder providerName(String providerName) {
			this.providerName = providerName;
			return this;
		}

		public Builder apiKey(String apiKey) {
			this.apiKey = apiKey;
			return this;
		}

		public Builder apiSecret(String apiSecret) {
			this.apiSecret = apiSecret;
			return this;
		}

		public Builder callbackURL(String callbackURL) {
			this.callbackURL = callbackURL;
			return this;
		}
		
		public Builder api(DefaultApi20 api) {
			this.api = api;
			return this;
		}

		public OAuthProvider build() {
			return new OAuthProvider(providerName, apiKey, apiSecret, callbackURL, api);
		}
	}
	
	@Override
	public String toString() {
		return "Builder [providerName=" + providerName + ", callbackURL=" + callbackURL + "]";
	}
}
