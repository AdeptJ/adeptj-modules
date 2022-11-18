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

package com.adeptj.modules.jaxrs.core.jwt.feature;

import com.adeptj.modules.jaxrs.core.jwt.filter.DynamicJwtClaimsIntrospectionFilter;
import com.adeptj.modules.jaxrs.core.jwt.filter.JwtClaimsIntrospectionFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.ext.Provider;
import java.lang.invoke.MethodHandles;

import static com.adeptj.modules.jaxrs.core.jwt.feature.JwtDynamicFeature.FEATURE_NAME;
import static jakarta.ws.rs.Priorities.AUTHORIZATION;
import static jakarta.ws.rs.RuntimeType.SERVER;

/**
 * A {@link DynamicFeature} that enables the {@link JwtClaimsIntrospectionFilter} for the configured JAX-RS filterMapping.
 * <p>
 * Note: AX-RS resource to methods filterMapping must be provided before bootstrapping RESTEasy.
 * {@link DynamicFeature#configure} will only be called once while RESTEasy bootstraps.
 * <p>
 * Therefore do not add any mapping once RESTEasy bootstrapped fully, those will be not be taken into account.
 * This is not the limitation of this feature but that is how RESTEasy works.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Provider
@ConstrainedTo(SERVER)
@Designate(ocd = JwtDynamicFeatureConfig.class)
@Component(property = FEATURE_NAME)
public class JwtDynamicFeature implements DynamicFeature {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    static final String FEATURE_NAME = "feature.name=JwtDynamicFeature";

    private static final String EQ = "=";

    private static final String ASTERISK = "*";

    private static final String FILTER_REG_MSG = "Registered DynamicJwtClaimsIntrospectionFilter for mapping [{}#{}]";

    private static final String SERVICE_FILTER = "(jwt.filter.type=dynamic)";

    /**
     * Each element should be in the form -
     * <p>
     * Resource's FQCN=resourceMethod1,resourceMethod1,...resourceMethodN.
     * <p>
     * if the JwtClaimsIntrospectionFilter has to be applied on given methods, otherwise just provide
     * the Resource's FQCN equals to [*] then the filter will be applied on all the resource methods.
     */
    private final String[] filterMapping;

    private final JwtClaimsIntrospectionFilter claimsIntrospectionFilter;

    @Activate
    public JwtDynamicFeature(@Reference(target = SERVICE_FILTER) JwtClaimsIntrospectionFilter filter,
                             @NotNull JwtDynamicFeatureConfig config) {
        this.claimsIntrospectionFilter = filter;
        this.filterMapping = config.filterMapping();
    }

    /**
     * Registers the {@link DynamicJwtClaimsIntrospectionFilter} for interception
     * in case of a match of configured resource class and methods.
     * <p>
     * See documentation on {@link JwtDynamicFeature#filterMapping} to know how the algo will work.
     *
     * @param resourceInfo containing the resource class and method
     * @param context      to register the DynamicJwtFilter for interception in case of a match
     */
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        if (ArrayUtils.isNotEmpty(this.filterMapping)) {
            String resource = resourceInfo.getResourceClass().getName();
            String method = resourceInfo.getResourceMethod().getName();
            for (String row : this.filterMapping) {
                String[] mapping = row.split(EQ);
                if (ArrayUtils.getLength(mapping) == 2) {
                    if (resource.equals(mapping[0]) && StringUtils.containsAny(mapping[1], method, ASTERISK)) {
                        context.register(this.claimsIntrospectionFilter, AUTHORIZATION);
                        LOGGER.info(FILTER_REG_MSG, resource, method);
                    }
                }
            }
        }
    }
}
