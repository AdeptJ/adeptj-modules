package com.adeptj.modules.data.mybatis.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "AdeptJ MyBatis Configuration",
        description = "AdeptJ MyBatis Configuration"
)
public @interface MyBatisConfig {

    String DEFAULT_ENV_ID = "development";

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
