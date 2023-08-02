package com.adeptj.modules.security.oauth.internal;

import com.adeptj.modules.security.oauth.NoSuchOAuthProviderException;
import com.adeptj.modules.security.oauth.OAuthProvider;
import com.adeptj.modules.security.oauth.OAuthProviderService;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.adeptj.modules.security.oauth.Constants.CFG_KEY_API_KEY;
import static com.adeptj.modules.security.oauth.Constants.CFG_KEY_API_SECRET;
import static com.adeptj.modules.security.oauth.Constants.CFG_KEY_CALLBACK;
import static com.adeptj.modules.security.oauth.Constants.CFG_KEY_DEBUG_OAUTH2_REQ;
import static com.adeptj.modules.security.oauth.Constants.CFG_KEY_PROVIDER_NAME;
import static com.adeptj.modules.security.oauth.Constants.CFG_KEY_SCOPES;
import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

/**
 * OAuthProviderServiceImpl.
 *
 * @author Rakesh Kumar, AdeptJ
 */
@ProviderType
@Component
public class OAuthProviderServiceImpl implements OAuthProviderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ConcurrentMap<String, OAuthProvider> providers;

    private final List<String> configPids;

    public OAuthProviderServiceImpl() {
        this.providers = new ConcurrentHashMap<>();
        this.configPids = new CopyOnWriteArrayList<>();
    }

    @Override
    public @NotNull OAuthProvider getProvider(String providerName) {
        OAuthProvider provider = this.providers.get(providerName);
        if (provider == null) {
            String msg = String.format("There is no such provider - (%s), configured providers are [%s]. " +
                    "Please configure a provider using the factory configuration (AdeptJ OAuth2 Provider Configurations)" +
                    " in the Felix WebConsole.", providerName, String.join(",", this.providers.keySet()));
            throw new NoSuchOAuthProviderException(msg);
        }
        return provider;
    }

    // <<------------------------------------------- OSGi Internal ------------------------------------------->>

    @Reference(service = OAuthProviderConfigFactory.class, cardinality = MULTIPLE, policy = DYNAMIC)
    protected void bindOAuthProviderConfigFactory(@NotNull Map<String, Object> properties) {
        String pid = (String) properties.get(SERVICE_PID);
        String providerName = (String) properties.get(CFG_KEY_PROVIDER_NAME);
        if (this.providers.containsKey(providerName)) {
            LOGGER.error("Provider({}) already exists, please consider using another provider." +
                    " The factory pid to identify this config in Felix WebConsole - ({}).", providerName, pid);
        } else {
            LOGGER.info("Binding OAuthProviderConfigFactory({}) with pid - {}", providerName, pid);
            String apiKey = (String) properties.get(CFG_KEY_API_KEY);
            String apiSecret = (String) properties.get(CFG_KEY_API_SECRET);
            String callback = (String) properties.get(CFG_KEY_CALLBACK);
            String[] scopes = (String[]) properties.get(CFG_KEY_SCOPES);
            boolean debug = Boolean.TRUE.equals(properties.get(CFG_KEY_DEBUG_OAUTH2_REQ));
            OAuthProviderImpl provider = new OAuthProviderImpl(providerName, apiKey, apiSecret, callback);
            provider.setScope(scopes);
            provider.setDebug(debug);
            this.providers.put(providerName, provider);
            this.configPids.add(pid);
            LOGGER.info("{} initialized!", provider);
        }
    }

    protected void unbindOAuthProviderConfigFactory(@NotNull Map<String, Object> properties) {
        String pid = (String) properties.get(SERVICE_PID);
        String providerName = (String) properties.get(CFG_KEY_PROVIDER_NAME);
        LOGGER.info("Unbinding OAuthProviderConfigFactory({}) with pid - {}", providerName, pid);
        if (this.configPids.remove(pid)) {
            this.providers.remove(providerName);
        }
    }

}
