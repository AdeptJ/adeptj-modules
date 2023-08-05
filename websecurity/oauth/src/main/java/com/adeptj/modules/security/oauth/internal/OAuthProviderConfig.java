package com.adeptj.modules.security.oauth.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

import static org.osgi.service.metatype.annotations.AttributeType.PASSWORD;

/**
 * OAuthProviderConfig
 *
 * @author Rakesh Kumar, AdeptJ
 */
@ObjectClassDefinition(
        name = "AdeptJ OAuth2 Provider Configurations",
        description = "Configurations for AdeptJ OAuth2 Provider"
)
public @interface OAuthProviderConfig {

    @AttributeDefinition(
            name = "OAuth2 Provider",
            description = "Oauth2 provider such as GitHub, Google etc.",
            options = {
                    @Option(label = "Google", value = "google"),
                    @Option(label = "Facebook", value = "facebook"),
                    @Option(label = "GitHub", value = "github"),
                    @Option(label = "Linkedin", value = "linkedin")
            })
    String provider_name();

    @AttributeDefinition(name = "OAuth2 Client Id", description = "Client id from OAuth2 provider.")
    String client_id();

    @AttributeDefinition(name = "OAuth2 Client Secret", description = "Client secret from OAuth2 provider.", type = PASSWORD)
    String client_secret();

    @AttributeDefinition(
            name = "OAuth2 Redirect URI",
            description = "A callback url where authorization code will be sent by the OAuth2 provider."
    )
    String redirect_uri() default "http://localhost:8080/oauth/callback";

    @AttributeDefinition(name = "OAuth2 Scopes", description = "Scopes configured in the OAuth2 provider.")
    String[] scopes();

    @AttributeDefinition(name = "Debug OAuth2 Request", description = "Whether to debug the OAuth2 request.")
    boolean debug_oauth2_request();

    // name hint non-editable property
    String webconsole_configurationFactory_nameHint() default "OAuth2 Provider: {" + "provider.name" + "}"; // NOSONAR

}
