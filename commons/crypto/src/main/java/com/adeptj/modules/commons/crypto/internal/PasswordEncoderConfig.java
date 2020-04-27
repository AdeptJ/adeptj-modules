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
import org.osgi.service.metatype.annotations.Option;

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

    String BCRYPT = "BCrypt";

    @AttributeDefinition(
            name = "BCrypt Exponential Cost",
            description = "The exponential cost (log2 factor) between 4 and 31 e.g. 12 will be 2^12 = 4096 iterations"
    )
    int bcrypt_exponential_cost() default 10;

    @AttributeDefinition(
            name = "PBEKeySpec Iteration Count",
            description = "The number of times that the given text is hashed during the derivation of the symmetric key."
    )
    int pbe_key_spec_iteration_count() default 150000;

    @AttributeDefinition(
            name = "PBE Key Length",
            description = "The PBE key length in bits for generating SecretKeySpec key.",
            options = {
                    @Option(label = "128", value = "128"),
                    @Option(label = "192", value = "192"),
                    @Option(label = "256", value = "256"),
                    @Option(label = "384", value = "384"),
                    @Option(label = "512", value = "512")
            }
    )
    int pbe_key_length() default 256;

    @AttributeDefinition(
            name = "Password Encoding Method",
            description = "Method to encode the password, only [BCrypt or PBKDF2WithHmacSHA*] supported at this moment.",
            options = {
                    @Option(label = "BCrypt", value = BCRYPT),
                    @Option(label = "PBKDF2 HmacSHA256", value = "PBKDF2WithHmacSHA256"),
                    @Option(label = "PBKDF2 HmacSHA384", value = "PBKDF2WithHmacSHA384"),
                    @Option(label = "PBKDF2 HmacSHA512", value = "PBKDF2WithHmacSHA512")
            }
    )
    String password_encoding_method() default BCRYPT;
}
