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

import com.adeptj.modules.jaxrs.core.JaxRSProvider;
import com.adeptj.modules.jaxrs.core.jwt.filter.JwtFilter;
import org.apache.commons.lang3.ArrayUtils;
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
import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

import static javax.ws.rs.Priorities.AUTHENTICATION;
import static org.apache.commons.lang3.StringUtils.containsAny;

/**
 * A {@link DynamicFeature} that enables the {@link JwtFilter} for the configured JAX-RS resourceVsMethodsMapping.
 * <p>
 * Note: AX-RS resource to methods resourceVsMethodsMapping must be provided before bootstrapping RESTEasy.
 * {@link DynamicFeature#configure} will only be called once while RESTEasy bootstraps.
 * <p>
 * Therefore do not add any mapping once RESTEasy bootstrapped fully, those will be not be taken into account.
 * This is not the limitation of this feature but that is how RESTEasy works.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@JaxRSProvider(name = "JwtDynamicFeature")
@Provider
@Designate(ocd = JwtDynamicFeatureConfig.class)
@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class JwtDynamicFeature implements DynamicFeature {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String EQ = "=";

    private static final String ASTERISK = "*";

    private static final String FILTER_REG_MSG = "Registered DynamicJwtFilter for mapping [{}#{}]";

    /**
     * Each element may be in the form - FQCN=resourceMethod1,resourceMethod1,...n if JwtFilter
     * has to be applied on given methods, otherwise just provide the resource class's
     * fully qualified name equals to [*] itself will apply the filter on all the resource methods.
     */
    private String[] resourceVsMethodsMapping;

    /**
     * Inject {@link com.adeptj.modules.jaxrs.core.jwt.filter.internal.DynamicJwtFilter}
     */
    @Reference(target = "(jwt.filter.name=dynamic)")
    private JwtFilter dynamicJwtFilter;

    /**
     * Registers the {@link com.adeptj.modules.jaxrs.core.jwt.filter.internal.DynamicJwtFilter} for interception
     * in case of a match of configured resource class and methods.
     * <p>
     * See documentation on {@link JwtDynamicFeature#resourceVsMethodsMapping} to know how the algo will work.
     *
     * @param resourceInfo containing the resource class and method
     * @param context      to register the DynamicJwtFilter for interception in case of a match
     */
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        String resource = resourceInfo.getResourceClass().getName();
        String method = resourceInfo.getResourceMethod().getName();
        Stream.of(this.resourceVsMethodsMapping)
                .filter(row -> ArrayUtils.getLength(row.split(EQ)) == 2)
                .map(row -> row.split(EQ))
                .filter(mapping -> resource.equals(mapping[0]) && containsAny(mapping[1], method, ASTERISK))
                .forEach(mapping -> {
                    context.register(this.dynamicJwtFilter, AUTHENTICATION);
                    LOGGER.info(FILTER_REG_MSG, resource, method);
                });
    }

    // ---------------------------------------- OSGi INTERNAL ----------------------------------------

    @Activate
    protected void start(JwtDynamicFeatureConfig config) {
        this.resourceVsMethodsMapping = config.resourceVsMethodsMapping();
    }
}
