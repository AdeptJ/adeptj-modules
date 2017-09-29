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

package com.adeptj.modules.aws.sns.api;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AWS {@link AsyncHandler} for SNS async calls. Default method behaviour is to just log things.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public interface AwsSnsAsyncHandler extends AsyncHandler<PublishRequest, PublishResult> {

    Logger LOGGER = LoggerFactory.getLogger(AwsSnsAsyncHandler.class);

    @Override
    default void onError(Exception exception) {
        LOGGER.error("Exception while sending SMS asynchronously!!", exception);
    }

    @Override
    default void onSuccess(PublishRequest request, PublishResult result) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("SMS sent to: [{}]", request.getPhoneNumber());
            LOGGER.debug("SNS PublishResult messageId: [{}]", result.getMessageId());
        }
    }
}
