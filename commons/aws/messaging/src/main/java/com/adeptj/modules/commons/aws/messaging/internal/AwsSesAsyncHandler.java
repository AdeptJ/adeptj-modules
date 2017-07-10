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

package com.adeptj.modules.commons.aws.messaging.internal;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AWS {@link AsyncHandler} for SES async calls.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class AwsSesAsyncHandler implements AsyncHandler<SendEmailRequest, SendEmailResult> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AwsSesAsyncHandler.class);

    @Override
    public void onError(Exception exception) {
        LOGGER.error("Exception while sending email asynchronously!!", exception);
    }

    @Override
    public void onSuccess(SendEmailRequest request, SendEmailResult result) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Email sent to: {}", request.getDestination().getToAddresses());
            LOGGER.debug("SES SendEmailResult messageId: [{}]", result.getMessageId());
        }
    }
}
