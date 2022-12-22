package com.adeptj.modules.jaxrs.core;

import com.adeptj.modules.jaxrs.core.jwt.resource.JwtCookieConfig;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

@Designate(ocd = JwtCookieConfig.class)
@Component(service = JwtCookieConfigService.class)
public class JwtCookieConfigService {

    private JwtCookieConfig cookieConfig;

    public JwtCookieConfig getJwtCookieConfig() {
        return cookieConfig;
    }

    // <<---------------------------------------- OSGi INTERNAL ------------------------------------------>>

    @Modified
    protected void update(@NotNull JwtCookieConfig cookieConfig) {
        this.cookieConfig = cookieConfig;
    }
}
