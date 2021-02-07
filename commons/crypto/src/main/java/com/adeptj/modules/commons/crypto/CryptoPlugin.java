package com.adeptj.modules.commons.crypto;

import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationPlugin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Dictionary;
import java.util.Iterator;

//@Component
public class CryptoPlugin implements ConfigurationPlugin {

    private final CryptoService cryptoService;

    @Activate
    public CryptoPlugin(@Reference CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @Override
    public void modifyConfiguration(ServiceReference<?> serviceReference, Dictionary<String, Object> dictionary) {
        for (Iterator<String> iterator = dictionary.keys().asIterator(); iterator.hasNext(); ) {
            String key = iterator.next();
            Object val = dictionary.get(key);
            if (val instanceof String) {
                String value = (String) val;
                if (value.startsWith("{sha256}")) {
                    dictionary.put(key, this.cryptoService.decrypt(value));
                }
            }
        }
    }
}
