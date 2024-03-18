package com.adeptj.modules.security.oauth.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;

import static com.adeptj.modules.security.oauth.internal.OAuthProviderConfigFactory.PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * OAuthProviderConfigFactory
 *
 * @author Rakesh Kumar, AdeptJ
 */
@Designate(ocd = OAuthProviderConfig.class, factory = true)
@Component(service = OAuthProviderConfigFactory.class, name = PID, configurationPolicy = REQUIRE)
public class OAuthProviderConfigFactory {
    static final String PID = "com.adeptj.modules.security.oauth.OAuthProviderConfigFactory.factory";

    public OAuthProviderConfigFactory() {
    }
}
