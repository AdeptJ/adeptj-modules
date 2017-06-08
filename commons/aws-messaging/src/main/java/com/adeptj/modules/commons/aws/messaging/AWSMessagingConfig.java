/*
###############################################################################
#                                                                             #
#    Copyright 2016, AdeptJ (http://adeptj.com)                               #
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
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

/**
 * @author Rakesh.Kumar, AdeptJ.
 */
@ObjectClassDefinition(name = "AWS Notification Services Configuration", description = "Configuration for AWS SES/SNS")
public @interface AWSMessagingConfig {

    // SNS Configs

    @AttributeDefinition(name = "snsRegion", description = "AWS SNS Region")
    String snsRegion();

    @AttributeDefinition(name = "senderId", description = "AWS SNS SenderID")
    String senderId();

    @AttributeDefinition(name = "SMSType", options = {@Option(label = "TRANSACTIONAL", value = "Transactional"),
            @Option(label = "PROMOTIONAL", value = "Promotional")})
    String smsType();

    @AttributeDefinition(name = "snsAccessKeyId", description = "AWS SNS AccessKeyId")
    String snsAccessKeyId();

    @AttributeDefinition(name = "snsSecretKey", description = "AWS SNS SecretKey")
    String snsSecretKey();

    // SES Configs

    @AttributeDefinition(name = "sesRegion", description = "AWS SES Region")
    String sesRegion();

    @AttributeDefinition(name = "from", description = "AWS SES Email From")
    String from();

    @AttributeDefinition(name = "sesAccessKeyId", description = "AWS SES AccessKeyId")
    String sesAccessKeyId();

    @AttributeDefinition(name = "sesSecretKey", description = "AWS SES SecretKey")
    String sesSecretKey();
}
