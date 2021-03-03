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
import com.adeptj.modules.commons.crypto.CryptoUtil;
import com.adeptj.modules.commons.crypto.KeyInitData;
import com.adeptj.modules.commons.utils.RandomUtil;
import com.adeptj.modules.commons.utils.annotation.ConfigPluginId;
import com.adeptj.modules.commons.utils.annotation.ConfigurationPluginProperties;
import com.adeptj.modules.commons.utils.annotation.WebConsolePlugin;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.felix.webconsole.AbstractWebConsolePlugin;
import org.apache.felix.webconsole.DefaultVariableResolver;
import org.apache.felix.webconsole.VariableResolver;
import org.apache.felix.webconsole.WebConsoleUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationPlugin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Dictionary;
import java.util.Iterator;

import static at.favre.lib.crypto.bcrypt.BCrypt.SALT_LENGTH;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;
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
@Component(
        immediate = true,
        service = {CryptoPlugin.class, Servlet.class, ConfigurationPlugin.class}
)
public class CryptoPlugin extends AbstractWebConsolePlugin implements ConfigurationPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    static final String PLUGIN_LABEL_VALUE = "crypto";

    static final String PLUGIN_TITLE_VALUE = "Crypto";

    static final String PLUGIN_ID = "adeptj-crypto-plugin";

    static final int SERVICE_RANKING = 400;

    private static final int IV_LENGTH = 12;

    private static final String CIPHER_ALGO = "AES/GCM/NoPadding";

    private static final String SECRET_KEY_SPEC_ALGO = "AES";

    private static final String CRYPTO_KEY_PROPERTY = "crypto.key";

    private static final String CRYPTO_ITERATIONS_PROPERTY = "crypto.iterations";

    private static final int PBE_KEY_LENGTH = 128;

    private static final int GCM_AUTH_TAG_LENGTH = PBE_KEY_LENGTH;

    private static final String PBE_ALGO = "PBKDF2WithHmacSHA256";

    /**
     * cpe = crypto plugin encrypted
     */
    private static final String ENCRYPTION_PREFIX = "{cpe}";

    private static final String KEY_PLAIN_TEXT = "plainText";

    private static final String KEY_CIPHER_TEXT = "cipherText";

    private static final String REQ_METHOD_GET = "GET";

    private static final String CRYPTO_HTML_LOCATION = "/templates/crypto.html";

    private final char[] cryptoKey;

    private final int iterations;

    @Activate
    public CryptoPlugin(BundleContext context) {
        this.cryptoKey = context.getProperty(CRYPTO_KEY_PROPERTY).toCharArray();
        this.iterations = Integer.parseInt(context.getProperty(CRYPTO_ITERATIONS_PROPERTY));
    }

    // << ---------------------------------- From CryptoPlugin ---------------------------------->>

    public boolean isEncrypted(String text) {
        return StringUtils.startsWith(text, ENCRYPTION_PREFIX);
    }

    public String encrypt(String plainText) {
        Validate.isTrue(StringUtils.isNotEmpty(plainText), "plainText can't be null!!");
        byte[] iv = null;
        byte[] salt = null;
        byte[] cipherBytes = null;
        byte[] compositeCipherBytes = null;
        try {
            // 1. get iv
            iv = RandomUtil.randomBytes(IV_LENGTH);
            // 2. get salt
            salt = RandomUtil.randomBytes(SALT_LENGTH);
            // 3. init encrypt mode cipher
            Cipher cipher = this.initCipher(salt, iv, ENCRYPT_MODE);
            // 4. generate cipher bytes
            cipherBytes = cipher.doFinal(plainText.getBytes(UTF_8));
            // 5. put everything in a ByteBuffer
            compositeCipherBytes = ByteBuffer.allocate(iv.length + salt.length + cipherBytes.length)
                    .put(iv)
                    .put(salt)
                    .put(cipherBytes)
                    .array();
            // 6. create an UTF-8 String after Base64 encoding the iv+salt+cipherBytes
            return ENCRYPTION_PREFIX + new String(Base64.getEncoder().encode(compositeCipherBytes), UTF_8);
        } catch (Exception ex) {
            throw new CryptoException(ex);
        } finally {
            CryptoUtil.nullSafeWipeAll(iv, salt, cipherBytes, compositeCipherBytes);
        }
    }

    public String decrypt(String cipherText) {
        Validate.isTrue(StringUtils.isNotEmpty(cipherText), "cipherText can't be null!!");
        if (!this.isEncrypted(cipherText)) {
            return cipherText;
        }
        cipherText = StringUtils.substringAfter(cipherText, ENCRYPTION_PREFIX);
        byte[] iv = null;
        byte[] salt = null;
        byte[] cipherBytes = null;
        byte[] decryptedBytes = null;
        try {
            // 1. Base64 decode the passed string.
            ByteBuffer buffer = ByteBuffer.wrap(Base64.getDecoder().decode(cipherText.getBytes(UTF_8)));
            iv = new byte[IV_LENGTH];
            // 2. extract iv
            buffer.get(iv);
            salt = new byte[SALT_LENGTH];
            // 3. extract salt
            buffer.get(salt);
            // 4. init decrypt mode cipher
            Cipher cipher = this.initCipher(salt, iv, DECRYPT_MODE);
            cipherBytes = new byte[buffer.remaining()];
            // 5. extract cipherBytes
            buffer.get(cipherBytes);
            // 6. decrypt cipherBytes
            decryptedBytes = cipher.doFinal(cipherBytes);
            // 7. create a UTF-8 String from decryptedBytes
            return new String(decryptedBytes, UTF_8);
        } catch (Exception ex) {
            throw new CryptoException(ex);
        } finally {
            CryptoUtil.nullSafeWipeAll(iv, salt, cipherBytes, decryptedBytes);
        }
    }

    // << ---------------------------------- From ConfigurationPlugin ---------------------------------->>

    @Override
    public void modifyConfiguration(ServiceReference<?> serviceReference, Dictionary<String, Object> properties) {
        Object pid = properties.get(SERVICE_PID);
        for (Iterator<String> iterator = properties.keys().asIterator(); iterator.hasNext(); ) {
            String key = iterator.next();
            Object value = properties.get(key);
            if (value instanceof String) {
                String oldValue = (String) value;
                String newValue = this.getPlainText(oldValue);
                if (!StringUtils.equals(oldValue, newValue)) {
                    properties.put(key, newValue);
                    LOGGER.info("Replaced value of configuration property '{}' for PID [{}]", key, pid);
                }
            } else if (value instanceof String[]) {
                String[] oldValues = (String[]) value;
                String[] newValues = null;
                for (int i = 0; i < oldValues.length; i++) {
                    String oldValue = oldValues[i];
                    String newValue = this.getPlainText(oldValue);
                    if (!StringUtils.equals(newValue, oldValue)) {
                        if (newValues == null) {
                            newValues = Arrays.copyOf(oldValues, oldValues.length);
                        }
                        newValues[i] = newValue;
                    }
                }
                if (newValues != null) {
                    properties.put(key, newValues);
                    LOGGER.info("Replaced value(s) of array type configuration property '{}' for PID [{}]", key, pid);
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
                cipherText = this.encrypt(plainText);
            } catch (CryptoException ce) {
                cipherText = "Exception while protecting Plain Text: " + ce;
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

    private Cipher initCipher(byte[] salt, byte[] iv, int mode) throws GeneralSecurityException {
        byte[] key = null;
        try {
            key = CryptoUtil.newSecretKeyBytes(KeyInitData.builder()
                    .algorithm(PBE_ALGO)
                    .password(this.cryptoKey)
                    .salt(salt)
                    .iterationCount(this.iterations)
                    .keyLength(PBE_KEY_LENGTH)
                    .build());
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, SECRET_KEY_SPEC_ALGO);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_AUTH_TAG_LENGTH, iv);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
            cipher.init(mode, secretKeySpec, parameterSpec);
            return cipher;
        } finally {
            CryptoUtil.nullSafeWipe(key);
        }
    }

    private String getPlainText(String cipherText) {
        if (StringUtils.isEmpty(cipherText)) {
            return cipherText;
        }
        String newValue;
        try {
            newValue = this.decrypt(cipherText);
        } catch (CryptoException ex) {
            newValue = cipherText;
            LOGGER.error(ex.getMessage(), ex);
        }
        return newValue;
    }

    // << ------------------------------------------ OSGi Internal ------------------------------------------>>

    @Deactivate
    protected void stop() {
        Arrays.fill(this.cryptoKey, Character.MIN_VALUE);
    }
}
