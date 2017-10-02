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

import com.adeptj.modules.aws.s3.api.StorageService;
import com.adeptj.modules.jaxrs.core.jwt.RequiresJwt;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;

import static com.adeptj.modules.aws.s3.UploadResource.RESOURCE_BASE;
import static javax.ws.rs.core.HttpHeaders.CONTENT_LENGTH;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;

/**
 * REST Endpoint for various operations on AWS S3.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Path("/aws/s3")
@Component(immediate = true, service = UploadResource.class, property = RESOURCE_BASE)
public class UploadResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadResource.class);

    static final String RESOURCE_BASE = "osgi.jaxrs.resource.base=aws-s3";

    @Reference
    private StorageService storageService;

    @POST
    @Path("/upload")
    @Consumes(MULTIPART_FORM_DATA)
    @RequiresJwt
    public Response uploadFile(MultipartFormDataInput multipart) {
        try {
            byte[] data = multipart.getFormDataPart("data", byte[].class, null);
            String bucketName = multipart.getFormDataPart("bucketName", String.class, null);
            String key = multipart.getFormDataPart("key", String.class, null);
            String cannedACL = multipart.getFormDataPart("access", String.class, null);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setHeader(CONTENT_LENGTH, (long) data.length);
            this.storageService.uploadFile(S3Request.builder()
                    .bucketName(bucketName)
                    .key(key)
                    .data(new ByteArrayInputStream(data))
                    .metadata(metadata)
                    .cannedACL(CannedAccessControlList.valueOf(cannedACL))
                    .build());
            return Response.ok("File uploaded successfully!!").build();
        } catch (Exception ex) {
            LOGGER.error("Exception while uploading file to S3!!");
        }
        return Response.ok("File can't be uploaded!!").build();
    }
}
