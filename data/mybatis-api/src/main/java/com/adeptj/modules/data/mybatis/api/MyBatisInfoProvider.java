package com.adeptj.modules.data.mybatis.api;

import org.osgi.annotation.versioning.ConsumerType;

import java.util.Set;

@ConsumerType
public interface MyBatisInfoProvider {

    String DEFAULT_MYBATIS_CONFIG = "META-INF/mybatis-config.xml";

    default String getConfigXmlLocation() {
        return DEFAULT_MYBATIS_CONFIG;
    }

    Set<Class<?>> getMappers();
}
