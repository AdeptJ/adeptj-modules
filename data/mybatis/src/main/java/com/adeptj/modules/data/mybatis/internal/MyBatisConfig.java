package com.adeptj.modules.data.mybatis.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import static com.adeptj.modules.data.mybatis.MyBatisInfoProvider.DEFAULT_MYBATIS_CONFIG;

@ObjectClassDefinition(
        name = "AdeptJ MyBatis Configuration",
        description = "AdeptJ MyBatis Configuration"
)
public @interface MyBatisConfig {

    String DEFAULT_ENV_ID = "development";

    @AttributeDefinition(
            name = "MyBatis Config XML Location",
            description = "Location of the MyBatis config xml file"
    )
    String configXmlLocation() default DEFAULT_MYBATIS_CONFIG;

    @AttributeDefinition(
            name = "Override Provider MyBatis XML Configuration",
            description = "Whether to override the MyBatis configuration provided by MyBatisInfoProvider impl"
    )
    boolean overrideProviderXmlConfig();

    @AttributeDefinition(
            name = "Disable MyBatis XML Configuration",
            description = "Whether to disable XML based MyBatis configuration"
    )
    boolean disableXmlConfiguration();

    @AttributeDefinition(
            name = "MyBatis Environment Identifier",
            description = "MyBatis symbolic environment identifier"
    )
    String environmentId() default DEFAULT_ENV_ID;
}
