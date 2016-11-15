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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * OSGiServletContextHelper for OSGi HttpServlet security.
 * 
 * @author Rakesh.Kumar, AdeptJ
 */
@Service(ServletContextHelper.class)
@Component
@Properties({ @Property(name = HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, value = "OSGiHttpContext"),
		@Property(name = HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, value = "/") })
public class OSGiServletContextHelper extends ServletContextHelper {

	@Override
	public String getMimeType(String name) {
		return super.getMimeType(name);
	}

	@Override
	public String getRealPath(String path) {
		return super.getRealPath(path);
	}

	@Override
	public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) throws IOException {
		return super.handleSecurity(request, response);
	}

}
