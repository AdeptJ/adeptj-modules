package com.adeptj.modules.data.mybatis.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "AdeptJ MyBatis Configuration",
        description = "AdeptJ MyBatis Configuration"
)
public @interface MyBatisConfig {

    String DEFAULT_MYBATIS_CONFIG = "META-INF/mybatis-config.xml";

    String DEFAULT_ENV_ID = "development";

    @AttributeDefinition(
            name = "MyBatis Config XML Location",
            description = "Location of the MyBatis config xml file"
    )
    String config_xml_location() default DEFAULT_MYBATIS_CONFIG;

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

    @AttributeDefinition(
            name = "MyBatis Properties",
            description = "Extra MyBatis Properties(key=value) format"
    )
    String[] mybatis_properties();
}
