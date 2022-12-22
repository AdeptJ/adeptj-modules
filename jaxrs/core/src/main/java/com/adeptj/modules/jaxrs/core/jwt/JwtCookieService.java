package com.adeptj.modules.jaxrs.core;

import jakarta.ws.rs.core.NewCookie;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import static jakarta.ws.rs.core.Cookie.DEFAULT_VERSION;

@Designate(ocd = JwtCookieConfig.class)
@Component(service = JwtCookieService.class)
public class JwtCookieService {

    private JwtCookieConfig cookieConfig;

    @Activate
    public JwtCookieService(@NotNull JwtCookieConfig cookieConfig) {
        this.cookieConfig = cookieConfig;
    }

    public String getJwtCookieName() {
        return this.cookieConfig.name();
    }

    public boolean isJwtCookieEnabled() {
        return this.cookieConfig.enabled();
    }

    public NewCookie createJwtCookie(String jwt) {
        return new NewCookie.Builder(this.getJwtCookieName())
                .value(jwt)
                .domain(this.cookieConfig.domain())
                .path(this.cookieConfig.path())
                .comment(this.cookieConfig.comment())
                .maxAge(this.cookieConfig.max_age())
                .secure(this.cookieConfig.secure())
                .httpOnly(this.cookieConfig.http_only())
                .sameSite(NewCookie.SameSite.LAX)
                .version(DEFAULT_VERSION)
                .build();
    }

    // <<---------------------------------------- OSGi INTERNAL ------------------------------------------>>

    @Modified
    protected void update(@NotNull JwtCookieConfig cookieConfig) {
        this.cookieConfig = cookieConfig;
    }
}
