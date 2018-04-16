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

package com.adeptj.modules.commons.utils.service.internal;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * OSGi Configuration for {@link com.adeptj.modules.commons.utils.service.CryptoService}
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ObjectClassDefinition(
        name = "AdeptJ Crypto Configuration",
        description = "Configuration for the CryptoService"
)
public @interface CryptoConfig {

    @AttributeDefinition(
            name = "Salt Size",
            description = "Size of the salt byte array"
    )
    int saltSize() default 32;

    @AttributeDefinition(
            name = "Iteration Count",
            description = "The number of times that the given text is hashed during the derivation of the symmetric key."
    )
    int iterationCount() default 10000;

    @AttributeDefinition(
            name = "Key Length",
            description = "The key length is the length of the derived symmetric key"
    )
    int keyLength() default 256;

    @AttributeDefinition(
            name = "Secret Key Algo",
            description = "Algo to generate the hash"
    )
    String secretKeyAlgo() default "PBKDF2WithHmacSHA256";
}
