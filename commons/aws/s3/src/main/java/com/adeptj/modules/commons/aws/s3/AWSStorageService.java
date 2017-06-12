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

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;

import java.io.InputStream;

/**
 * API for storing data in AWS S3 buckets.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public interface AWSStorageService {

    Bucket createBucket(String bucketName);

    void deleteBucket(String bucketName);

    void createRecord(String bucketName, String key, InputStream data);

    S3Object getRecord(String bucketName, String key);

    void deleteRecord(String bucketName, String key);
}
