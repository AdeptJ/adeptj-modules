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

package com.adeptj.modules.commons.crypto.internal;

import com.adeptj.modules.commons.crypto.PasswordEncoder;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * OSGi Configuration for {@link PasswordEncoder}
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ObjectClassDefinition(
        name = "AdeptJ PasswordEncoder Configuration",
        description = "Configuration for the AdeptJ PasswordEncoder"
)
public @interface PasswordEncoderConfig {

    @AttributeDefinition(
            name = "Exponential Cost",
            description = "The exponential cost (log2 factor) between 4 and 31 e.g. 12 will be 2^12 = 4096 rounds, " +
                    "keep the cost factor reasonable as with each increment it would take twice the amount of time to compute. " +
                    "Default value is 10, which should be sufficient for most of the use cases."
    )
    int exponential_cost() default 10;
}
