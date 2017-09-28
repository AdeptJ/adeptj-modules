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

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.InputStream;

/**
 * AWS S3 request object to be consumed by {@link com.adeptj.modules.aws.s3.api.StorageService}
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public final class S3Request {

    private String bucketName;

    private String folderName;

    private String key;

    private ObjectMetadata metadata;

    private InputStream data;

    private CannedAccessControlList cannedACL;

    private S3Request(String bucketName, String key) {
        this.bucketName = bucketName;
        this.key = key;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getFolderName() {
        return folderName;
    }

    public String getKey() {
        return key;
    }

    public ObjectMetadata getMetadata() {
        return metadata;
    }

    public InputStream getData() {
        return data;
    }

    public CannedAccessControlList getCannedACL() {
        return cannedACL;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for creating {@link S3Request} fluently.
     */
    public static final class Builder {

        private String bucketName;

        private String folderName;

        private String key;

        private ObjectMetadata metadata;

        private InputStream data;

        private CannedAccessControlList cannedACL;

        // no public access as available through static method.
        private Builder() {
        }

        public Builder bucketName(String bucketName) {
            this.bucketName = bucketName;
            return this;
        }

        public Builder folderName(String folderName) {
            this.folderName = folderName;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder metadata(ObjectMetadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder data(InputStream data) {
            this.data = data;
            return this;
        }

        public Builder cannedACL(CannedAccessControlList cannedACL) {
            this.cannedACL = cannedACL;
            return this;
        }

        public S3Request build() {
            S3Request s3Request = new S3Request(this.bucketName, this.key);
            s3Request.folderName = this.folderName;
            s3Request.metadata = metadata;
            s3Request.data = this.data;
            s3Request.cannedACL = this.cannedACL;
            return s3Request;
        }
    }
}
