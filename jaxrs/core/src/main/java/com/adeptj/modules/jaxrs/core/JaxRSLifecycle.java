package com.adeptj.modules.jaxrs.core;

import org.osgi.annotation.versioning.ConsumerType;

@ConsumerType
public interface JaxRSLifecycle {

    void start(Object config);

    void stop();

    <T> T unwrap(Class<T> type);
}
