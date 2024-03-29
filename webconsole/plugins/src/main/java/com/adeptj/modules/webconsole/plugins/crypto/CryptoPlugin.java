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
package com.adeptj.modules.webconsole.plugins.crypto;

import com.adeptj.modules.commons.crypto.CryptoException;
import com.adeptj.modules.commons.crypto.CryptoService;
import com.adeptj.modules.commons.utils.annotation.ConfigPluginId;
import com.adeptj.modules.commons.utils.annotation.ConfigurationPluginProperties;
import com.adeptj.modules.commons.utils.annotation.WebConsolePlugin;
import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.webconsole.servlet.AbstractServlet;
import org.apache.felix.webconsole.servlet.RequestVariableResolver;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationPlugin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serial;
import java.lang.invoke.MethodHandles;
import java.util.Dictionary;
import java.util.Iterator;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.osgi.framework.Constants.SERVICE_PID;

/**
 * The CryptoPlugin.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@HttpWhiteboardResource(pattern = "/crypto-plugin/*", prefix = "static")
@ConfigurationPluginProperties(service_cmRanking = CryptoPlugin.SERVICE_RANKING)
@WebConsolePlugin(label = CryptoPlugin.PLUGIN_LABEL_VALUE, title = CryptoPlugin.PLUGIN_TITLE_VALUE)
@ConfigPluginId(CryptoPlugin.PLUGIN_ID)
@Component(service = {Servlet.class, ConfigurationPlugin.class})
public class CryptoPlugin extends AbstractServlet implements ConfigurationPlugin {

    @Serial
    private static final long serialVersionUID = 282533706713570062L;

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    static final String PLUGIN_LABEL_VALUE = "crypto";

    static final String PLUGIN_TITLE_VALUE = "Crypto";

    static final String PLUGIN_ID = "adeptj-crypto-plugin";

    static final int SERVICE_RANKING = 400;

    /**
     * cpe = crypto plugin encrypted
     */
    private static final String ENCRYPTION_PREFIX = "{cpe}";

    private static final String KEY_PLAIN_TEXT = "plainText";

    private static final String KEY_CIPHER_TEXT = "cipherText";

    private static final String GET = "GET";

    private static final String CRYPTO_HTML_LOCATION = "/templates/crypto.html";

    private final CryptoService cryptoService;

    @Activate
    public CryptoPlugin(@Reference CryptoService cryptoService) {
        this.cryptoService = cryptoService;
        LOGGER.info("AdeptJ CryptoPlugin initialized!!");
    }

    // << ---------------------------------- From ConfigurationPlugin ---------------------------------->>

    @Override
    public void modifyConfiguration(ServiceReference<?> serviceReference, @NotNull Dictionary<String, Object> properties) {
        Object pid = properties.get(SERVICE_PID);
        for (Iterator<String> iterator = properties.keys().asIterator(); iterator.hasNext(); ) {
            String key = iterator.next();
            Object value = properties.get(key);
            if (value instanceof String val) {
                this.handleProperty(properties, pid, key, val);
            } else if (value instanceof String[] values) {
                this.handleMultiValueProperty(pid, key, values);
            }
        }
    }

    private void handleProperty(Dictionary<String, Object> properties, Object pid, String key, String currentValue) {
        if (StringUtils.startsWith(currentValue, ENCRYPTION_PREFIX)) {
            String newValue = this.decrypt(currentValue);
            if (StringUtils.isNotEmpty(newValue) && !StringUtils.equals(currentValue, newValue)) {
                properties.put(key, newValue);
                LOGGER.info("Decrypted the value of configuration property '{}' for PID [{}]", key, pid);
            }
        }
    }

    private void handleMultiValueProperty(Object pid, String key, String @NotNull [] currentValues) {
        for (int i = 0; i < currentValues.length; i++) {
            String currentValue = currentValues[i];
            if (StringUtils.startsWith(currentValue, ENCRYPTION_PREFIX)) {
                String newValue = this.decrypt(currentValue);
                if (StringUtils.isNotEmpty(newValue) && !StringUtils.equals(newValue, currentValue)) {
                    // update the array with the new value.
                    currentValues[i] = newValue;
                    LOGGER.info("Decrypted the value at index [{}] of multi value configuration property '{}' for PID [{}]",
                            i, key, pid);
                }
            }
        }
    }

    private String decrypt(String cipherText) {
        String plainText = null;
        try {
            cipherText = StringUtils.substringAfter(cipherText, ENCRYPTION_PREFIX).trim();
            if (StringUtils.isNotEmpty(cipherText)) {
                plainText = this.cryptoService.decrypt(cipherText);
            }
        } catch (CryptoException ex) {
            plainText = cipherText;
            LOGGER.error(ex.getMessage(), ex);
        }
        return plainText;
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
        res.getWriter().print(super.readTemplateFile(CRYPTO_HTML_LOCATION));
    }

    // << -------------------------------------- From HttpServlet -------------------------------------->>

    @Override
    protected void doPost(@NotNull HttpServletRequest req, HttpServletResponse resp) {
        String plainText = req.getParameter(KEY_PLAIN_TEXT);
        String cipherText = EMPTY;
        if (StringUtils.isNotEmpty(plainText)) {
            try {
                cipherText = ENCRYPTION_PREFIX + this.cryptoService.encrypt(plainText);
            } catch (CryptoException ce) {
                cipherText = "Exception while encrypting Plain Text: " + ce;
            }
        }
        this.populateVariableResolver(req, plainText, cipherText);
    }

    private void populateVariableResolver(HttpServletRequest req, String plainText, String cipherText) {
        RequestVariableResolver vr = this.getVariableResolver(req);
        if (vr == null) {
            LOGGER.warn("Cannot set plainText and cipherText attributes as the RequestVariableResolver is null!");
        } else {
            vr.put(KEY_PLAIN_TEXT, plainText);
            vr.put(KEY_CIPHER_TEXT, cipherText);
        }
    }
}
