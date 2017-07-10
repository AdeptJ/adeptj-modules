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
package com.adeptj.modules.commons.aws.messaging;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

/**
 * OSGi configurations for AWS messaging service.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ObjectClassDefinition(name = "AdeptJ AWS Notification Services Configuration", description = "Configuration for AWS SES/SNS")
public @interface AWSMessagingConfig {

    // SNS Configs

    @AttributeDefinition(name = "snsServiceEndpoint", description = "AWS SNS Service Endpoint")
    String snsServiceEndpoint() default "sns.us-west-2.amazonaws.com";

    @AttributeDefinition(name = "snsSigningRegion", description = "AWS SNS Signing Region used for SigV4 signing of requests")
    String snsSigningRegion() default "us-west-2";

    @AttributeDefinition(name = "senderId", description = "AWS SNS SenderID, check your country if SenderID is supported or not")
    String senderId();

    @AttributeDefinition(name = "SMSType", description = "SMS priority", options = {
            @Option(label = "TRANSACTIONAL", value = "Transactional"),
            @Option(label = "PROMOTIONAL", value = "Promotional")
    })
    String smsType();

    // SES Configs

    @AttributeDefinition(name = "from", description = "AWS SES Email From")
    String from();

    @AttributeDefinition(name = "sesServiceEndpoint", description = "AWS SES Service Endpoint")
    String sesServiceEndpoint() default "email.us-west-2.amazonaws.com";

    @AttributeDefinition(name = "sesSigningRegion", description = "AWS SES Signing Region used for SigV4 signing of requests")
    String sesSigningRegion() default "us-west-2";

    // Common Configs

    @AttributeDefinition(name = "accessKeyId", description = "AWS AccessKeyId")
    String accessKeyId();

    @AttributeDefinition(name = "secretKey", description = "AWS SecretKey", type = AttributeType.PASSWORD)
    String secretKey();

}
