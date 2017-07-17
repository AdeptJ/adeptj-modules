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

import java.util.Objects;

/**
 * SmsRequest object consumed by {@link com.adeptj.modules.aws.sns.api.SmsService}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class SmsRequest {

    private String countryCode;

    private String phoneNumber;

    private String message;

    public SmsRequest(String countryCode, String phoneNumber, String message) {
        this.countryCode = Objects.requireNonNull(countryCode, () -> "countryCode can't be null!!");
        this.phoneNumber = Objects.requireNonNull(phoneNumber, () -> "phoneNumber can't be null!!");;
        this.message = Objects.requireNonNull(message, () -> "message can't be null!!");;
    }

    public String getCountryCode() {
        return countryCode.startsWith("+") ? countryCode : "+" + countryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getMessage() {
        return message;
    }
}
