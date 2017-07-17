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

package com.adeptj.modules.aws.ses;

import com.adeptj.modules.aws.ses.api.EmailService;

import java.util.Objects;

/**
 * EmailRequest object consumed by {@link EmailService}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class EmailRequest {

    private String recipient;

    private String subject;

    private String body;

    public EmailRequest(String recipient, String subject, String body) {
        this.recipient = Objects.requireNonNull(recipient, () -> "recipient can't be null!!");;
        this.subject = Objects.requireNonNull(subject, () -> "subject can't be null!!");;
        this.body = Objects.requireNonNull(body, () -> "body can't be null!!");;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }
}
