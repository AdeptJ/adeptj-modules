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
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

import java.io.InputStream;

/**
 * API for storing data in AWS S3 buckets.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public interface AwsStorageService {

    /**
     * Create a new bucket on S3.
     *
     * @param bucketName
     *                   Bucket Name.
     *
     * @return
     */
    Bucket createBucket(String bucketName);

    /**
     * Delete an existing bucket on S3.
     *
     * @param bucketName
     *                     Bucket Name.
     */
    void deleteBucket(String bucketName);

    /**
     * Create a new record on S3.
     *
     * @param bucketName
     *                      Bucket Name.
     * @param key
     *              Record Key.
     * @param data
     *              Record Data.
     * @param acl
     *              Record ACL.
     */
    void createRecord(String bucketName, String key, InputStream data, RecordACL acl);

    /**
     * Create a new record on S3 with metadata.
     *
     * @param bucketName
     *                      Bucket Name.
     * @param key
     *              Record Key.
     * @param metadata
     *               Record Metadata.
     * @param data
     *              Record Data.
     * @param acl
     *              Record ACL.
     */
    void createRecord(String bucketName, String key, ObjectMetadata metadata,
                      InputStream data, RecordACL acl);

    /**
     * Get a record from S3.
     *
     * @param bucketName
     *                      Bucket Name.
     * @param key
     *                      Record Key.
     * @return
     */
    S3Object getRecord(String bucketName, String key);

    /**
     * Delete a record from S3.
     *
     * @param bucketName
     *                      Bucket Name.
     * @param key
     *                      Record Key.
     */
    void deleteRecord(String bucketName, String key);

    /**
     * get Server End point.
     *
     * @return
     */
    String getServiceEndpoint();

    /**
     * Get Signing Region.
     *
     * @return
     */
    String getSigningRegion();
}
