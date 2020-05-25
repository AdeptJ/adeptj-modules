package com.adeptj.modules.data.mybatis;

import org.osgi.annotation.versioning.ConsumerType;

@ConsumerType
public interface MyBatisInfoProvider {

    String DEFAULT_MYBATIS_CONFIG = "META-INF/mybatis-config.xml";

    default String getMyBatisConfig() {
        return DEFAULT_MYBATIS_CONFIG;
    }

    String getEnvironmentId();
}
