package com.adeptj.modules.security.oauth.internal;

import com.adeptj.modules.security.oauth.OAuthProvider;
import com.adeptj.modules.security.oauth.OAuthProviderService;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

import static com.adeptj.modules.security.oauth.internal.OAuthProviderServiceImpl.SERVICE_PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * OAuthProviderFactoryImpl.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ProviderType
@Designate(ocd = OAuthProviderConfig.class, factory = true)
@Component(service = OAuthProviderService.class, name = SERVICE_PID, configurationPolicy = REQUIRE)
public class OAuthProviderServiceImpl implements OAuthProviderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    static final String SERVICE_PID = "com.adeptj.modules.oauth.provider.OAuthProviderService.factory";

    private final OAuthProviderConfig config;

    @Activate
    public OAuthProviderServiceImpl(OAuthProviderConfig config) {
        LOGGER.info("Initializing OAuthProviderService for ({})", config.provider());
        this.config = config;
    }

    @Override
    public OAuthProvider getOAuthProvider() {
        return OAuthProvider.builder(this.config.provider())
                .apiKey(this.config.api_key())
                .apiSecret(this.config.api_secret())
                .callback(this.config.callback())
                .build();
    }

}
