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
package com.adeptj.modules.commons.aws.s3;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * @author Rakesh.Kumar, AdeptJ.
 */
@ObjectClassDefinition(name = "AdeptJ AWS Storage Service Configuration", description = "Configuration for AWS S3")
public @interface AWSStorageConfig {

    // AWS S3 Configs

    @AttributeDefinition(name = "serviceEndpoint", description = "AWS S3 Service Endpoint")
    String serviceEndpoint() default "s3.us-west-2.amazonaws.com";

    @AttributeDefinition(name = "signingRegion", description = "AWS S3 Signing Region used for SigV4 signing of requests")
    String signingRegion() default "us-west-2";

    @AttributeDefinition(name = "accessKeyId", description = "AWS S3 AccessKeyId")
    String accessKeyId();

    @AttributeDefinition(name = "secretKey", description = "AWS S3 SecretKey")
    String secretKey();
}
