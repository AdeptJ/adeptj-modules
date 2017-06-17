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
package com.adeptj.modules.security.shiro.common;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.servlet.ShiroHttpSession;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extended form for {@link SimpleCookie} removes the machine name appended by
 * the container to the JSESSIONID.
 * 
 * @author Rakesh.Kumar, AdeptJ..
 */
public class SimpleCookieExt extends SimpleCookie {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleCookieExt.class);

	/**
	 * JSESSIONID Predicate.
	 */
	private static final Predicate<Cookie> JSESSIONID_PREDICATE = (cookie) -> {
		return StringUtils.equals(ShiroHttpSession.DEFAULT_SESSION_ID_NAME, cookie.getName());
	};

	/**
	 * Removes the machine name appended to the JSESSIONID.
	 */
	@Override
	public String readValue(HttpServletRequest request, HttpServletResponse ignored) {
		String name = getName();
		String value = null;
		Cookie cookie = getCookie(request, name);
		if (cookie != null) {
			value = StringUtils.substringBefore(cookie.getValue(), ".");
			LOGGER.debug("After removing (.) if any, Found '{}' cookie value [{}]", name, value);
		} else {
			LOGGER.trace("No '{}' cookie value", name);
		}
		return value;
	}

	/**
	 * Returns the cookie with the given name from the request or {@code null}
	 * if no cookie with that name could be found.
	 *
	 * @param request
	 *            the current executing HTTP request.
	 * @param cookieName
	 *            the name of the cookie to find and return.
	 * @return the cookie with the given name from the request or {@code null}
	 *         if no cookie with that name could be found.
	 */
	private Cookie getCookie(HttpServletRequest request, String cookieName) {
		Cookie cookies[] = request.getCookies();
		Cookie jsessionidCookie = null;
		if (cookies != null) {
			Optional<Cookie> cookieOpt = Arrays.stream(cookies).filter(JSESSIONID_PREDICATE).findFirst();
			if (cookieOpt.isPresent()) {
				jsessionidCookie = cookieOpt.get();
			}
		}

		return jsessionidCookie;
	}

}