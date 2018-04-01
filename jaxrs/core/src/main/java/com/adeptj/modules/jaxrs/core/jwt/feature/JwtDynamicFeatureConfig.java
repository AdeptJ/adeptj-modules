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

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Configuration specifying the JAX-RS resource class to methods mapping.There can be n numbers of such mappings.
 * <p>
 * Must be in the specified format as given below.
 * <p>
 * JAX-RS resource FQCN=resourceMethod1,resourceMethod2,...n.
 * <p>
 * Alternatively, can be the resource FQCN alone, in that case the filter will be applied on all the resource methods.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ObjectClassDefinition(
        name = "AdeptJ JWT DynamicFeature Config",
        description = "AdeptJ JWT DynamicFeature Config"
)
@interface JwtDynamicFeatureConfig {

    @AttributeDefinition(
            name = "Resource Vs Methods Mapping",
            description = "JAX-RS Resource vs Methods Mapping. Must be in the format: FQCN=resourceMethod1,resourceMethod2,...n"
    )
    String[] resourceVsMethodsMapping() default {};
}
