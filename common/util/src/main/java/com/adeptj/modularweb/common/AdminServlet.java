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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * AdminServlet.
 * 
 * @author Rakesh.Kumar, AdeptJ
 */
@Service
@Component
@Properties({ @Property(name = HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, value = "AdminServlet"),
		@Property(name = HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, value = "/admin/*"),})
public class AdminServlet extends HttpServlet {
	
	private static final long serialVersionUID = 8684006452376829341L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.getOutputStream().write(bytes(AdminServlet.class.getResourceAsStream("/adminweb/views/welcome.html")));
	}
	
	public static byte[] bytes(InputStream input) throws IOException {
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int numberOfBytesRead;
		while ((numberOfBytesRead = input.read(buffer)) != -1) {
			out.write(buffer, 0, numberOfBytesRead);
		}
		return out.toByteArray();
	}

}
