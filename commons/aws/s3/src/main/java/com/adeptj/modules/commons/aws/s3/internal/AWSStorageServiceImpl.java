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
package com.adeptj.modules.commons.aws.s3.internal;

import com.adeptj.modules.commons.aws.s3.AWSStorageConfig;
import com.adeptj.modules.commons.aws.s3.AWSStorageService;
import com.adeptj.modules.commons.aws.s3.RecordACL;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * AWSStorageService for storing data in AWS S3 buckets.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
@Designate(ocd = AWSStorageConfig.class)
@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class AWSStorageServiceImpl implements AWSStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AWSStorageServiceImpl.class);

    private AmazonS3 s3Client;

    /**
     * {@inheritDoc}
     */
    @Override
    public Bucket createBucket(String bucketName) {
        LOGGER.info("Creating Bucket: [{}]!!", bucketName);
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
    public void createRecord(String bucketName, String key, InputStream data,
                             RecordACL acl) {
        PutObjectRequest objectRequest = new PutObjectRequest(bucketName, key, data,
                new ObjectMetadata());
        objectRequest.setCannedAcl(this.getS3Acl(acl));
        this.s3Client.putObject(objectRequest);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createRecord(String bucketName, String key, ObjectMetadata metadata,
                             InputStream data, RecordACL acl) {
        PutObjectRequest objectRequest = new PutObjectRequest(bucketName, key, data, metadata);
        objectRequest.setCannedAcl(this.getS3Acl(acl));
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
    protected void activate(AWSStorageConfig config) {
        this.s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(config.accessKeyId(), config.secretKey()))).withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(config
                .serviceEndpoint(), config.signingRegion())).build();
    }

    private CannedAccessControlList getS3Acl(RecordACL acl) {
        CannedAccessControlList accessControlList = null;
        switch (acl) {

            case AuthenticatedRead:
                accessControlList = CannedAccessControlList.AuthenticatedRead;
                break;
            case AwsExecRead:
                accessControlList = CannedAccessControlList.AwsExecRead;
                break;
            case BucketOwnerFullControl:
                accessControlList = CannedAccessControlList.BucketOwnerFullControl;
                break;
            case BucketOwnerRead:
                accessControlList = CannedAccessControlList.BucketOwnerRead;
                break;
            case LogDeliveryWrite:
                accessControlList = CannedAccessControlList.LogDeliveryWrite;
                break;
            case Private:
                accessControlList = CannedAccessControlList.Private;
                break;
            case PublicRead:
                accessControlList = CannedAccessControlList.PublicRead;
                break;
            case PublicReadWrite:
                accessControlList = CannedAccessControlList.PublicReadWrite;
                break;
            default:
                accessControlList = CannedAccessControlList.Private;
                break;
        }
        return accessControlList;
    }

    @Deactivate
    protected void deactivate() {
        this.s3Client.shutdown();
    }
}
