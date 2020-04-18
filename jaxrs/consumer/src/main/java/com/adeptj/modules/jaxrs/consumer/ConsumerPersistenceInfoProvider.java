package com.adeptj.modules.jaxrs.consumer;

import com.adeptj.modules.data.jpa.PersistenceInfoProvider;
import org.osgi.service.component.annotations.Component;

import java.util.Map;

@Component
public class ConsumerPersistenceInfoProvider implements PersistenceInfoProvider {

    @Override
    public String getPersistenceUnitName() {
        return "AdeptJ_PU_MySQL";
    }

    @Override
    public Map<String, Object> getPersistenceUnitProperties() {
        return null;
    }
}
