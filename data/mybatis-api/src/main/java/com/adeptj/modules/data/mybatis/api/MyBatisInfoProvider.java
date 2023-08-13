package com.adeptj.modules.data.mybatis.api;

import org.osgi.annotation.versioning.ConsumerType;

import java.util.Set;

@ConsumerType
public interface MyBatisInfoProvider {

    Set<Class<?>> getMappers();
}
