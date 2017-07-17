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

import com.adeptj.modules.aws.s3.S3Config;
import com.adeptj.modules.aws.s3.api.StorageService;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

/**
 * StorageService for storing files in AWS S3 buckets.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@Designate(ocd = S3Config.class)
@Component(immediate = true, configurationPolicy = REQUIRE)
public class AwsS3Service implements StorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AwsS3Service.class);

    private AmazonS3 s3Client;

    /**
     * {@inheritDoc}
     */
    @Override
    public Bucket createBucket(String bucketName) {
        LOGGER.info("Creating Bucket: [{}]", bucketName);
        return this.s3Client.createBucket(bucketName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteBucket(String bucketName) {
        this.s3Client.deleteBucket(bucketName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createRecord(String bucketName, String key, InputStream data) {
        PutObjectRequest objectRequest = new PutObjectRequest(bucketName, key, data, new ObjectMetadata());
        objectRequest.setCannedAcl(null);
        this.s3Client.putObject(objectRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createRecord(String bucketName, String key, ObjectMetadata metadata, InputStream data) {
        PutObjectRequest objectRequest = new PutObjectRequest(bucketName, key, data, metadata);
        objectRequest.setCannedAcl(null);
        this.s3Client.putObject(objectRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public S3Object getRecord(String bucketName, String key) {
        return this.s3Client.getObject(bucketName, key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteRecord(String bucketName, String key) {
        this.s3Client.deleteObject(bucketName, key);
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
