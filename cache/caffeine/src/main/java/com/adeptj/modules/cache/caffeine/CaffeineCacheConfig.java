package com.adeptj.modules.cache.caffeine;

import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "AdeptJ CaffeineCache Configuration",
        description = "Configuration for AdeptJ CaffeineCache."
)
public @interface CaffeineCacheConfig {

    int expireAfter();

    int maximumSize();
}
