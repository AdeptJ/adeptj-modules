package com.adeptj.modules.commons.validator.service;

import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition
public @interface ValidatorConfig {

    boolean ignoreXmlConfiguration();
}
