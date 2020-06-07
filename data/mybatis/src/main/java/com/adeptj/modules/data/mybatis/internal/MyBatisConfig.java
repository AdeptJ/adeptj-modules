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
    String config_xml_location() default DEFAULT_MYBATIS_CONFIG;

    @AttributeDefinition(
            name = "Override Provider MyBatis Config XML Location",
            description = "Whether to override the MyBatis config xml location provided by MyBatisInfoProvider"
    )
    boolean override_provider_config_xml_location();

    @AttributeDefinition(
            name = "Disable MyBatis XML Configuration",
            description = "Whether to disable XML based MyBatis configuration"
    )
    boolean disable_xml_configuration();

    @AttributeDefinition(
            name = "MyBatis Environment Identifier",
            description = "MyBatis symbolic environment identifier"
    )
    String environment_id() default DEFAULT_ENV_ID;
}
