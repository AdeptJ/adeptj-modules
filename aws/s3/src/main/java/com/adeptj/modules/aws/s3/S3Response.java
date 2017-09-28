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

package com.adeptj.modules.aws.s3;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;

/**
 * AWS S3 response holder.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class S3Response {

    private Bucket bucket;

    private S3Object s3Object;

    private PutObjectResult putObjectResult;

    public Bucket getBucket() {
        return bucket;
    }

    public void setBucket(Bucket bucket) {
        this.bucket = bucket;
    }

    public S3Response withBucket(Bucket bucket) {
        this.setBucket(bucket);
        return this;
    }

    public S3Object getS3Object() {
        return s3Object;
    }

    public void setS3Object(S3Object s3Object) {
        this.s3Object = s3Object;
    }

    public S3Response withS3Object(S3Object s3Object) {
        this.setS3Object(s3Object);
        return this;
    }

    public PutObjectResult getPutObjectResult() {
        return putObjectResult;
    }

    public void setPutObjectResult(PutObjectResult putObjectResult) {
        this.putObjectResult = putObjectResult;
    }

    public S3Response withPutObjectResult(PutObjectResult putObjectResult) {
        this.setPutObjectResult(putObjectResult);
        return this;
    }
}
