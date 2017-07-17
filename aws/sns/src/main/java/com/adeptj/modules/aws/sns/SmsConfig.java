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
package com.adeptj.modules.aws.sns;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

import static org.osgi.service.metatype.annotations.AttributeType.PASSWORD;

/**
 * OSGi configurations for AWS Simple Notification Service.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ObjectClassDefinition(
        name = "AdeptJ AWS SNS Configuration",
        description = "Configuration for AWS Simple Notification Service"
)
public @interface SmsConfig {

    @AttributeDefinition(
            name = "serviceEndpoint",
            description = "AWS SNS Service Endpoint e.g [sns.us-west-2.amazonaws.com]"
    )
    String serviceEndpoint();

    @AttributeDefinition(
            name = "signingRegion",
            description = "AWS SNS Signing Region used for SigV4 signing of requests e.g [us-west-2]")
    String signingRegion();

    @AttributeDefinition(
            name = "senderId",
            description = "AWS SNS SenderID, check your country if SenderID is supported or not"
    )
    String senderId();

    @AttributeDefinition(name = "SMSType", description = "SMS priority", options = {
            @Option(label = "TRANSACTIONAL", value = "Transactional"),
            @Option(label = "PROMOTIONAL", value = "Promotional")
    })
    String smsType();

    @AttributeDefinition(name = "accessKey", description = "AWS AccessKey")
    String accessKey();

    @AttributeDefinition(name = "secretKey", description = "AWS SecretKey", type = PASSWORD)
    String secretKey();
}
