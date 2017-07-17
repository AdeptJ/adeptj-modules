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

package com.adeptj.modules.aws.sns;

import java.util.Map;

/**
 * EmailRequest object returned by {@link com.adeptj.modules.aws.sns.api.SmsService}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class SmsResponse {

    private final String messageId;

    private final int httpStatusCode;

    private final Map<String, String> httpHeaders;

    public SmsResponse(String messageId, int httpStatusCode, Map<String, String> httpHeaders) {
        this.messageId = messageId;
        this.httpStatusCode = httpStatusCode;
        this.httpHeaders = httpHeaders;
    }

    public String getMessageId() {
        return messageId;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }
}
