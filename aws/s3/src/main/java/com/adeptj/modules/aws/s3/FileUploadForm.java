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

import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;

/**
 * File upload form bean.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class FileUploadForm {

    private static final String PARAM_DATA = "data";

    private static final String PARAM_BUCKET_NAME = "bucketName";

    private static final String PARAM_KEY = "key";

    private static final String PARAM_CANNED_ACL = "access";

    private byte[] data;

    private String bucketName;

    private String key;

    private String access;

    public byte[] getData() {
        return data;
    }

    @FormParam(PARAM_DATA)
    @PartType("application/octet-stream")
    public void setData(byte[] data) {
        this.data = data;
    }

    public String getBucketName() {
        return bucketName;
    }

    @FormParam(PARAM_BUCKET_NAME)
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getKey() {
        return key;
    }

    @FormParam(PARAM_KEY)
    public void setKey(String key) {
        this.key = key;
    }

    public String getAccess() {
        return access;
    }

    @FormParam(PARAM_CANNED_ACL)
    public void setAccess(String access) {
        this.access = access;
    }
}
