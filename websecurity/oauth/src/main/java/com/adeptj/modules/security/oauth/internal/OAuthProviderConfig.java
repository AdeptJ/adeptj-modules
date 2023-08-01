package com.adeptj.modules.security.oauth.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

import static org.osgi.service.metatype.annotations.AttributeType.PASSWORD;

@ObjectClassDefinition(
        name = "AdeptJ OAuth2 Provider Service Configurations",
        description = "Configurations for AdeptJ OAuth2 Provider Service"
)
public @interface OAuthProviderConfig {

    @AttributeDefinition(
            name = "OAuth2 Provider",
            description = "Oauth2 provider such as GitHub, Google etc.",
            options = {
                    @Option(label = "GitHub", value = "github"),
                    @Option(label = "Google", value = "google"),
                    @Option(label = "Facebook", value = "facebook"),
                    @Option(label = "Linkedin", value = "linkedin")
            })
    String provider();

    @AttributeDefinition(name = "OAuth2 API Key", description = "OAuth2 API Key.")
    String api_key();

    @AttributeDefinition(name = "OAuth2 API Secret", description = "OAuth2 API Secret.", type = PASSWORD)
    String api_secret();

    @AttributeDefinition(name = "OAuth2 Scopes", description = "Scopes configured in the OAuth2 provider.")
    String[] scopes();

    @AttributeDefinition(name = "OAuth2 Callback", description = "OAuth2 callback url.")
    String callback() default "http://localhost:8080/oauth2/callback";

    // name hint non-editable property
    String webconsole_configurationFactory_nameHint() default "OAuth2 Provider: {" + "provider" + "}"; // NOSONAR

}
