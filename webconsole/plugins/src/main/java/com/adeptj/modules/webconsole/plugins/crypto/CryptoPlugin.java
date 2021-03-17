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
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.webconsole.AbstractWebConsolePlugin;
import org.apache.felix.webconsole.DefaultVariableResolver;
import org.apache.felix.webconsole.VariableResolver;
import org.apache.felix.webconsole.WebConsoleUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationPlugin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
public class CryptoPlugin extends AbstractWebConsolePlugin implements ConfigurationPlugin {

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

    private static final String REQ_METHOD_GET = "GET";

    private static final String CRYPTO_HTML_LOCATION = "/templates/crypto.html";

    private final CryptoService cryptoService;

    @Activate
    public CryptoPlugin(@Reference CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    // << ---------------------------------- From ConfigurationPlugin ---------------------------------->>

    @Override
    public void modifyConfiguration(ServiceReference<?> serviceReference, Dictionary<String, Object> properties) {
        Object pid = properties.get(SERVICE_PID);
        for (Iterator<String> iterator = properties.keys().asIterator(); iterator.hasNext(); ) {
            String key = iterator.next();
            Object value = properties.get(key);
            if (value instanceof String) {
                this.handleProperty(properties, pid, key, (String) value);
            } else if (value instanceof String[]) {
                this.handleMultiValueProperty(pid, key, (String[]) value);
            }
        }
    }

    private void handleProperty(Dictionary<String, Object> properties, Object pid, String key, String oldValue) {
        if (StringUtils.startsWith(oldValue, ENCRYPTION_PREFIX)) {
            String newValue = this.decrypt(oldValue);
            if (!StringUtils.equals(oldValue, newValue)) {
                properties.put(key, newValue);
                LOGGER.info("Decrypted value of configuration property '{}' for PID [{}]", key, pid);
            }
        }
    }

    private void handleMultiValueProperty(Object pid, String key, String[] oldValues) {
        for (int i = 0; i < oldValues.length; i++) {
            String oldValue = oldValues[i];
            if (StringUtils.startsWith(oldValue, ENCRYPTION_PREFIX)) {
                String newValue = this.decrypt(oldValue);
                if (!StringUtils.equals(newValue, oldValue)) {
                    oldValues[i] = newValue;
                    LOGGER.info("Decrypted value at index [{}] of multi value configuration property '{}' for PID '{}'",
                            i, key, pid);
                }
            }
        }
    }

    // << ---------------------------------- From AbstractWebConsolePlugin ---------------------------------->>

    @Override
    public String getLabel() {
        return PLUGIN_LABEL_VALUE;
    }

    @Override
    public String getTitle() {
        return PLUGIN_TITLE_VALUE;
    }

    @Override
    protected void renderContent(HttpServletRequest req, HttpServletResponse res) throws IOException {
        this.populateVariableResolverWithDefaultValues(req);
        res.getWriter().print(super.readTemplateFile(CRYPTO_HTML_LOCATION));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String plainText = req.getParameter(KEY_PLAIN_TEXT);
        String cipherText = EMPTY;
        if (StringUtils.isNotEmpty(plainText)) {
            try {
                cipherText = ENCRYPTION_PREFIX + this.cryptoService.encrypt(plainText);
            } catch (CryptoException ce) {
                cipherText = "Exception while encrypting Plain Text: " + ce;
            }
        }
        this.populateVariableResolverAfterPost(req, plainText, cipherText);
        // re-render the form again with populated data in DefaultVariableResolver.
        super.doGet(req, resp);
    }

    @SuppressWarnings("unchecked")
    private void populateVariableResolverWithDefaultValues(HttpServletRequest req) {
        // Put the default values in DefaultVariableResolver while rendering the form.
        // That will be a GET request but when this method is called via a doGet after the form submission
        // then the request method is still POST so below logic will not be executed.
        if (REQ_METHOD_GET.equals(req.getMethod())) {
            VariableResolver vr = WebConsoleUtil.getVariableResolver(req);
            if (vr instanceof DefaultVariableResolver) {
                DefaultVariableResolver dvr = (DefaultVariableResolver) vr;
                dvr.put(KEY_PLAIN_TEXT, EMPTY);
                dvr.put(KEY_CIPHER_TEXT, EMPTY);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void populateVariableResolverAfterPost(HttpServletRequest req, String plainText, String cipherText) {
        VariableResolver vr = WebConsoleUtil.getVariableResolver(req);
        if (vr instanceof DefaultVariableResolver) {
            DefaultVariableResolver dvr = (DefaultVariableResolver) vr;
            dvr.put(KEY_PLAIN_TEXT, plainText);
            dvr.put(KEY_CIPHER_TEXT, cipherText);
        }
    }

    private String decrypt(String cipherText) {
        String plainText;
        try {
            cipherText = StringUtils.substringAfter(cipherText, ENCRYPTION_PREFIX).trim();
            plainText = this.cryptoService.decrypt(cipherText);
        } catch (CryptoException | IllegalArgumentException ex) {
            plainText = cipherText;
            LOGGER.error(ex.getMessage(), ex);
        }
        return plainText;
    }
}
