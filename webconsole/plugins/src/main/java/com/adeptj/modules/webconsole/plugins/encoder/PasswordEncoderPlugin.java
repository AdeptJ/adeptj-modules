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
package com.adeptj.modules.webconsole.plugins.encoder;

import com.adeptj.modules.commons.crypto.PasswordEncoder;
import com.adeptj.modules.commons.utils.annotation.ConfigPluginId;
import com.adeptj.modules.commons.utils.annotation.WebConsolePlugin;
import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.webconsole.servlet.AbstractServlet;
import org.apache.felix.webconsole.servlet.RequestVariableResolver;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * The CryptoPlugin.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@WebConsolePlugin(label = PasswordEncoderPlugin.PLUGIN_LABEL_VALUE, title = PasswordEncoderPlugin.PLUGIN_TITLE_VALUE)
@ConfigPluginId(PasswordEncoderPlugin.PLUGIN_ID)
@Component(service = Servlet.class)
public class PasswordEncoderPlugin extends AbstractServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    static final String PLUGIN_LABEL_VALUE = "encoder";

    static final String PLUGIN_TITLE_VALUE = "Password Encoder";

    static final String PLUGIN_ID = "adeptj-password-encoder-plugin";

    private static final String KEY_PLAIN_TEXT = "plainText";

    private static final String KEY_ENCODED_TEXT = "encodedText";

    private static final String GET = "GET";

    private static final String ENCODER_HTML_LOCATION = "/templates/encoder.html";

    private final PasswordEncoder passwordEncoder;

    @Activate
    public PasswordEncoderPlugin(@Reference PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        LOGGER.info("AdeptJ PasswordEncoderPlugin initialized!!");
    }

    // << ---------------------------------- From AbstractServlet ---------------------------------->>

    @Override
    public void renderContent(@NotNull HttpServletRequest req, HttpServletResponse res) throws IOException {
        // Put the default values in RequestVariableResolver while rendering the form (a GET request).
        // But when this method is called via a doGet call after the form submission (in doPost method below)
        // then the request method is still POST, so population with empty values will not happen.
        if (GET.equals(req.getMethod())) {
            this.populateVariableResolver(req, EMPTY, EMPTY);
        }
        res.getWriter().print(super.readTemplateFile(ENCODER_HTML_LOCATION));
    }

    // << -------------------------------------- From HttpServlet -------------------------------------->>

    @Override
    protected void doPost(@NotNull HttpServletRequest req, HttpServletResponse resp) {
        String plainText = req.getParameter(KEY_PLAIN_TEXT);
        String encodedText = EMPTY;
        if (StringUtils.isNotEmpty(plainText)) {
            try {
                encodedText = this.passwordEncoder.encode(plainText);
            } catch (Exception ex) { // NOSONAR
                encodedText = "Exception while encoding Plain Text: " + ex;
            }
        }
        this.populateVariableResolver(req, plainText, encodedText);
    }

    private void populateVariableResolver(HttpServletRequest req, String plainText, String encodedText) {
        RequestVariableResolver vr = this.getVariableResolver(req);
        if (vr == null) {
            LOGGER.warn("Cannot set plainText and encodedText attributes as the RequestVariableResolver is null!");
        } else {
            vr.put(KEY_PLAIN_TEXT, plainText);
            vr.put(KEY_ENCODED_TEXT, encodedText);
        }
    }
}
