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

package com.adeptj.modules.webconsole.security;

import org.apache.felix.webconsole.SimpleWebConsolePlugin;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.apache.felix.webconsole.WebConsoleConstants.PLUGIN_LABEL;
import static org.apache.felix.webconsole.WebConsoleConstants.PLUGIN_TITLE;

/**
 * AdeptJ Tools Plugin.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Component(
        immediate = true,
        service = Servlet.class,
        property = {
                PLUGIN_LABEL + "=" + "tools",
                PLUGIN_TITLE + "=" + "AdeptJ Tools"
        }
)
public class ToolsPlugin extends SimpleWebConsolePlugin {

    private static final long serialVersionUID = 8041033223220201144L;

    public ToolsPlugin() {
        super("tools", "AdeptJ Tools", "Main", null);
    }

    @Override
    protected void renderContent(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.sendRedirect("/tools/dashboard");
    }

}
