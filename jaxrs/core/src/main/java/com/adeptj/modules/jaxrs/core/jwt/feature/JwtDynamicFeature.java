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

import com.adeptj.modules.jaxrs.core.jwt.filter.JwtFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.stream.Stream;

import static com.adeptj.modules.jaxrs.core.jwt.feature.JwtDynamicFeature.PROVIDER_OSGI_PROPERTY;
import static javax.ws.rs.Priorities.AUTHENTICATION;

/**
 * A {@link DynamicFeature} that enables the {@link JwtFilter} for the configured JAX-RS mappings.
 * <p>
 * Note: AX-RS resource to methods mappings must be provided before bootstrapping RESTEasy.
 * {@link DynamicFeature#configure} will only be called once while RESTEasy bootstraps.
 * <p>
 * Therefore do not add any mapping once RESTEasy bootstrapped fully, those will be not be taken into account.
 * This is not the limitation of this feature but that is how RESTEasy works.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Provider
@Designate(ocd = JwtDynamicFeatureConfig.class)
@Component(
        immediate = true,
        configurationPolicy = ConfigurationPolicy.REQUIRE,
        property = PROVIDER_OSGI_PROPERTY
)
public class JwtDynamicFeature implements DynamicFeature {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtDynamicFeature.class);

    private static final String EQ = "=";

    private static final String COMMA = ",";

    private static final String FILTER_REG_MSG = "Registered DynamicJwtFilter for mapping [{}#{}]";

    static final String PROVIDER_OSGI_PROPERTY = "osgi.jaxrs.provider=jwt-dyna-feature";

    /**
     * Each element may be in the form - FQCN=resourceMethod1,resourceMethod1,...n if JwtFilter
     * has to be applied on given methods, otherwise just provide the resource class's
     * fully qualified name itself which will apply the filter on all the resource methods.
     */
    private String[] mappings;

    /**
     * Inject {@link com.adeptj.modules.jaxrs.core.jwt.filter.internal.DynamicJwtFilter}
     */
    @Reference(target = "(jwt.filter.name=dynamic)")
    private JwtFilter jwtFilter;

    /**
     * Registers the {@link com.adeptj.modules.jaxrs.core.jwt.filter.internal.DynamicJwtFilter} for interception
     * in case of a match of configured resource class and methods.
     * <p>
     * See documentation on {@link JwtDynamicFeature#mappings} to know how will the algo work.
     *
     * @param resourceInfo containing the resource class and method
     * @param context      to register the DynamicJwtFilter for interception in case of a match
     */
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        String resource = resourceInfo.getResourceClass().getName();
        String method = resourceInfo.getResourceMethod().getName();
        Stream.of(this.mappings)
                .forEach(mapping -> {
                    String[] resVsMethods = mapping.split(EQ);
                    if (ArrayUtils.getLength(resVsMethods) == 2) {
                        // Format: key=val1,val2,...n
                        // At index 0 there will be FQCN of resource class.
                        // At index 1 there will be comma separated method names.
                        if (resource.equals(resVsMethods[0]) && Arrays.asList(resVsMethods[1].split(COMMA)).contains(method)) {
                            context.register(this.jwtFilter, AUTHENTICATION);
                            LOGGER.info(FILTER_REG_MSG, resource, method);
                        }
                    } else if (StringUtils.equals(mapping, resource)) {
                        // only FQCN of resource class, register the filter for all the resource methods.
                        context.register(this.jwtFilter, AUTHENTICATION);
                        LOGGER.info(FILTER_REG_MSG, resource, method);
                    }
                });
    }

    // -------------------- INTERNAL --------------------

    // Component Lifecycle Methods

    @Activate
    protected void start(JwtDynamicFeatureConfig config) {
        this.mappings = config.resourceToMethodsMapping();
    }
}
