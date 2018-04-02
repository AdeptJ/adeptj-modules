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
import com.adeptj.modules.jaxrs.core.JaxRSException;
import com.adeptj.modules.jaxrs.core.jwt.RequiresJwt;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;

import static com.adeptj.modules.aws.s3.UploadResource.AWS_S3_ROOT;
import static com.adeptj.modules.aws.s3.UploadResource.RESOURCE_BASE;
import static com.adeptj.modules.jaxrs.core.JaxRSConstants.STATUS_SERVER_ERROR;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;

/**
 * REST Endpoint for various operations on AWS S3.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Path(AWS_S3_ROOT)
@Component(immediate = true, service = UploadResource.class, property = RESOURCE_BASE)
public class UploadResource {

    private static final String PATH_UPLOAD = "/upload";

    static final String RESOURCE_BASE = "osgi.jaxrs.resource.base=aws-s3";

    static final String AWS_S3_ROOT = "/aws/s3";

    @Reference
    private StorageService storageService;

    @POST
    @Path(PATH_UPLOAD)
    @Consumes(MULTIPART_FORM_DATA)
    @RequiresJwt
    public Response uploadFile(@MultipartForm FileUploadForm form) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength((long) form.getData().length);
            this.storageService.uploadFile(S3Request.builder()
                    .bucketName(form.getBucketName())
                    .key(form.getKey())
                    .data(IOUtils.buffer(new ByteArrayInputStream(form.getData())))
                    .metadata(metadata)
                    .cannedACL(CannedAccessControlList.valueOf(form.getAccess()))
                    .build());
            return Response.ok("File uploaded successfully!!").build();
        } catch (Exception ex) { // NOSONAR
            throw JaxRSException.builder()
                    .message(ex.getMessage())
                    .cause(ex)
                    .status(STATUS_SERVER_ERROR)
                    .logException(true)
                    .build();
        }
    }
}
