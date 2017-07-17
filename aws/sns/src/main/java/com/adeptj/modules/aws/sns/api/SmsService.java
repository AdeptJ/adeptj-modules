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
package com.adeptj.modules.aws.sns.api;

import com.adeptj.modules.aws.sns.SmsRequest;
import com.adeptj.modules.aws.sns.SmsResponse;

/**
 * Service for sending SMS via AWS SNS.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public interface SmsService {

    /**
     * Sends the sms synchronously
     *
     * @param smsRequest contains requisite data by AWS SNS
     */
    SmsResponse sendSms(SmsRequest smsRequest);

    /**
     * Sends the sms asynchronously.
     *
     * @param smsRequest contains requisite data by AWS SNS
     */
    void sendSmsAsync(SmsRequest smsRequest);
}
