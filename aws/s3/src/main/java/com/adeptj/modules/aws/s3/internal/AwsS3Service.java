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
package com.adeptj.modules.aws.s3.internal;

import com.adeptj.modules.aws.core.AwsException;
import com.adeptj.modules.aws.s3.S3Config;
import com.adeptj.modules.aws.s3.UploadRequest;
import com.adeptj.modules.aws.s3.api.StorageService;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * StorageService implementation for various operations on AWS S3.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = S3Config.class)
@Component(immediate = true, configurationPolicy = REQUIRE)
public class AwsS3Service implements StorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AwsS3Service.class);

    private static final String PATH_SEPARATOR = "/";

    private static final String ACL_NULL_MSG = "CannedAccessControlList can't be null!!";

    private static final String OBJ_METADATA_NULL_MSG = "ObjectMetadata can't be null!!";

    private static final String DATA_NULL_MSG = "Data can't be null!!";

    private AmazonS3 s3Client;

    /**
     * {@inheritDoc}
     */
    @Override
    public Bucket createBucket(String bucketName) {
        try {
            return this.s3Client.createBucket(bucketName);
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while creating bucket!!", ex);
            throw new AwsException(ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteBucket(String bucketName) {
        try {
            this.s3Client.deleteBucket(bucketName);
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while deleting bucket!!", ex);
            throw new AwsException(ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PutObjectResult createFolder(String bucketName, String folderName) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(0);
        try {
            return this.s3Client.putObject(new PutObjectRequest(bucketName,
                    folderName + PATH_SEPARATOR, new ByteArrayInputStream(new byte[0]), objectMetadata));
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while creating folder!!", ex);
            throw new AwsException(ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PutObjectResult uploadFile(UploadRequest request) {
        InputStream data = Objects.requireNonNull(request.getData(), DATA_NULL_MSG);
        ObjectMetadata objectMetadata = Objects.requireNonNull(request.getMetadata(), OBJ_METADATA_NULL_MSG);
        CannedAccessControlList cannedACL = Objects.requireNonNull(request.getCannedACL(), ACL_NULL_MSG);
        try {
            return this.s3Client.putObject(new PutObjectRequest(request.getBucketName(), request.getKey(),
                    data, objectMetadata)
                    .withCannedAcl(cannedACL));
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while uploading file!!", ex);
            throw new AwsException(ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S3Object getFile(String bucketName, String key) {
        try {
            return this.s3Client.getObject(bucketName, key);
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while getting file!!", ex);
            throw new AwsException(ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteFile(String bucketName, String key) {
        try {
            this.s3Client.deleteObject(bucketName, key);
        } catch (RuntimeException ex) {
            LOGGER.error("Exception while deleting file!!", ex);
            throw new AwsException(ex.getMessage(), ex);
        }
    }

    // Lifecycle Methods

    @Activate
    protected void start(S3Config config) {
        try {
            this.s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(config.accessKey(),
                            config.secretKey())))
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(config.serviceEndpoint(),
                            config.signingRegion()))
                    .build();
        } catch (Throwable ex) { //NOSONAR
            LOGGER.error("Exception while creating S3 client!!", ex);
        }
    }

    @Deactivate
    protected void stop() {
        this.s3Client.shutdown();
    }
}
