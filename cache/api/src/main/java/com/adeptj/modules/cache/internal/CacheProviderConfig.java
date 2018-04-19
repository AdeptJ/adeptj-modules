package com.adeptj.modules.cache.internal;

import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "AdeptJ CacheProvider Configuration",
        description = "Configuration for AdeptJ CacheProvider."
)
@interface CacheProviderConfig {

    String[] cacheProviders() default {
            "EHCAHE",
            "CAFFEINE",
    };

}
