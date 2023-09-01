/*
###############################################################################
#                                                                             # 
#    Copyright 2016, AdeptJ (http://www.adeptj.com)                           #
#                                                                             #
#    Licensed under the Apache License, Version 2.0 (the "License");          #
#    you may not use this file except in compliance with the License.         #
#    You may obtain a copy of the License at                                  #
#                                                                             #
#        http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                             #
#    Unless required by applicable law or agreed to in writing, software      #
#    distributed under the License is distributed on an "AS IS" BASIS,        #
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
#    See the License for the specific language governing permissions and      #
#    limitations under the License.                                           #
#                                                                             #
###############################################################################
*/

package com.adeptj.modules.webconsole.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.webconsole.WebConsoleSecurityProvider;
import org.apache.felix.webconsole.spi.SecurityProvider;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import java.security.Principal;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Stream;

import static jakarta.servlet.http.HttpServletResponse.SC_FOUND;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Felix {@link SecurityProvider} implementation which matches the roles set in request with the configured ones.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = WebConsoleSecurityConfig.class)
@Component(
        immediate = true,
        service = SecurityProvider.class,
        property = {
                RoleBasedSecurityProvider.PN_SECURITY_PROVIDER_ID + "=" + RoleBasedSecurityProvider.SECURITY_PROVIDER_ID
        }
)
public class RoleBasedSecurityProvider implements SecurityProvider {

    static final String PN_SECURITY_PROVIDER_ID = "webconsole.security.provider.id";

    static final String SECURITY_PROVIDER_ID = "adeptj-webconsole-security-provider";

    private static final String HEADER_LOC = "Location";

    private static final String ADMIN = "admin";

    private String[] roles;

    private String logoutURI;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean authorize(Object user, String role) {
        return true;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // store in a local var as it might be updated by the modified method, see the update method in this class.
        String tempLogoutURI = this.logoutURI;
        // Note: Semantics of this method states that Session invalidation should not happen here.
        // Not using response.sendRedirect due to exception handling we need to do, avoiding that.
        // Set the status to [302] and location header to [/admin/logout] so that browser could redirect there.
        // AdminServlet will take care of Session invalidation later.
        response.setStatus(SC_FOUND);
        response.setHeader(HEADER_LOC, tempLogoutURI);
    }

    @Override
    public Object authenticate(HttpServletRequest request, HttpServletResponse response) {
        // store in a local var as it might be updated by the modified method, see the update method in this class.
        String[] tempRoles = this.roles;
        if (Stream.of(tempRoles).anyMatch(request::isUserInRole)) {
            return (Principal) () -> ADMIN;
        }
        return null;
    }

    // <<------------------------------------------- OSGi Internal ------------------------------------------>>

    @Activate
    @Modified
    protected void start(@NotNull WebConsoleSecurityConfig config) {
        this.roles = config.roles();
        this.logoutURI = config.logout_uri();
        if (StringUtils.isNotEmpty(config.admin_password())) {
            try (MVStore store = MVStore.open(config.credentials_store_name())) {
                MVMap<String, String> credentials = store.openMap(config.credentials_map_name());
                byte[] encoded = Base64.getEncoder().encode(DigestUtils.sha256(config.admin_password()));
                credentials.put(ADMIN, new String(encoded, UTF_8));
                Arrays.fill(encoded, (byte) 0);
            }
        }
    }

}
