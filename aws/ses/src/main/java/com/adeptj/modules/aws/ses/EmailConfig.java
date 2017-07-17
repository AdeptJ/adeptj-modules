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
package com.adeptj.modules.aws.ses;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import static org.osgi.service.metatype.annotations.AttributeType.PASSWORD;

/**
 * OSGi configurations for AWS Simple Email Service.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ObjectClassDefinition(
        name = "AdeptJ AWS SES Configuration",
        description = "Configuration for AWS Simple Email Service"
)
public @interface EmailConfig {

    @AttributeDefinition(name = "from", description = "Email sent from")
    String from();

    @AttributeDefinition(
            name = "serviceEndpoint",
            description = "AWS SES Service Endpoint e.g. [email.us-west-2.amazonaws.com]"
    )
    String serviceEndpoint();

    @AttributeDefinition(
            name = "signingRegion",
            description = "AWS SES Signing Region used for SigV4 signing of requests e.g. [us-west-2]"
    )
    String signingRegion();

    @AttributeDefinition(name = "accessKey", description = "AWS AccessKey")
    String accessKey();

    @AttributeDefinition(name = "secretKey", description = "AWS SecretKey", type = PASSWORD)
    String secretKey();
}
