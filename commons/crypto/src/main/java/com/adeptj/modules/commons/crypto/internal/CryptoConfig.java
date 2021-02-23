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

import com.adeptj.modules.commons.crypto.CryptoService;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

/**
 * OSGi Configuration for {@link CryptoService}
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ObjectClassDefinition(
        name = "AdeptJ CryptoService Configuration",
        description = "Configuration for the AdeptJ CryptoService"
)
public @interface CryptoConfig {

    @AttributeDefinition(
            name = "AES Key Length",
            description = "The AES key length in bits for generating SecretKeySpec key.",
            options = {
                    @Option(label = "128", value = "128"),
                    @Option(label = "192", value = "192"),
                    @Option(label = "256", value = "256")
            }
    )
    int aes_key_length() default 128;

    @AttributeDefinition(
            name = "GCM Authentication Tag Length",
            description = "The authentication tag length in bits for GCMParameterSpec.",
            options = {
                    @Option(label = "128", value = "128"),
                    @Option(label = "120", value = "120"),
                    @Option(label = "112", value = "112"),
                    @Option(label = "104", value = "104"),
                    @Option(label = "96", value = "96")
            }
    )
    int auth_tag_length() default 128;

    @AttributeDefinition(
            name = "PBEKeySpec Algorithm",
            description = "PBE algorithm for generating SecretKeySpec, only [PBKDF2WithHmacSHA*] are supported at this moment.",
            options = {
                    @Option(label = "PBKDF2 HmacSHA256", value = "PBKDF2WithHmacSHA256"),
                    @Option(label = "PBKDF2 HmacSHA384", value = "PBKDF2WithHmacSHA384"),
                    @Option(label = "PBKDF2 HmacSHA512", value = "PBKDF2WithHmacSHA512")
            }
    )
    String pbe_key_spec_algorithm() default "PBKDF2WithHmacSHA256";

    @AttributeDefinition(
            name = "PBEKeySpec Iteration Count",
            description = "The number of times that the given text is hashed during the derivation of the symmetric key."
    )
    int pbe_key_spec_iteration_count() default 10000;

    @AttributeDefinition(
            name = "Crypto Key",
            description = "The crypto key will be used for encryption and decryption. " +
                    "This has been interpolated via AdeptJ Felix Framework properties, if needed, please use other" +
                    " interpolation methods as described here - https://github.com/apache/felix-dev/tree/master/configadmin-plugins/interpolation"
    )
    String crypto_key() default "$[prop:crypto.key]";
}
