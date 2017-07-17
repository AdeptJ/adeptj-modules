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
package com.adeptj.modules.aws.s3;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import static org.osgi.service.metatype.annotations.AttributeType.PASSWORD;

/**
 * @author Rakesh.Kumar, AdeptJ.
 */
@ObjectClassDefinition(
        name = "AdeptJ AWS S3 Configuration",
        description = "Configuration for AWS Simple Storage Service"
)
public @interface S3Config {

    @AttributeDefinition(
            name = "serviceEndpoint",
            description = "AWS S3 Service Endpoint e.g [s3.us-west-2.amazonaws.com]"
    )
    String serviceEndpoint();

    @AttributeDefinition(
            name = "signingRegion",
            description = "AWS S3 Signing Region used for SigV4 signing of requests e.g [us-west-2]"
    )
    String signingRegion();

    @AttributeDefinition(name = "accessKey", description = "AWS S3 AccessKey")
    String accessKey();

    @AttributeDefinition(name = "secretKey", description = "AWS S3 SecretKey", type = PASSWORD)
    String secretKey();
}
