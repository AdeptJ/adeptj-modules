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
package com.adeptj.modularweb.common.servlet.async;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
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
		@Property(name = HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, value = "/osgi-async/*"),
		@Property(name = HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED, value = "true")})
public class OSGiAsyncHttpServlet extends HttpServlet {
	
	private static final long serialVersionUID = -9102820266874910703L;
	
	// private static final Logger LOGGER = LoggerFactory.getLogger(AdminServlet.class);
	
	static int counter = 1;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//System.out.println("Start Async using thread: " + Thread.currentThread());
		AsyncContext asyncCtx = req.startAsync();
		ServletOutputStream outputStream = resp.getOutputStream();
		outputStream.setWriteListener(new WriteListener() {
			
			@Override
			public void onWritePossible() throws IOException {
				if (outputStream.isReady()) {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("WriteListener.onWritePossible: " + Thread.currentThread());
					outputStream.write("Hello from AdminServlet!!".getBytes(StandardCharsets.UTF_8));
					asyncCtx.complete();
				}
			}
			
			@Override
			public void onError(Throwable t) {
				System.out.println("WriteListener onError: " + Thread.currentThread());
				asyncCtx.complete();
			}
		});
		System.out.println("Served request no. : " + counter);
		counter++;
		//asyncCtx.complete();
	}

}
