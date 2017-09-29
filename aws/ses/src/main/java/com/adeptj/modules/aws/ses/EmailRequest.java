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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * EmailRequest object consumed by {@link EmailService}.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class EmailRequest {

    private List<String> recipientToList;

    private List<String> recipientCcList;

    private List<String> recipientBccList;

    private String subject;

    private String body;

    private EmailRequest(String subject, String body, List<String> recipientToList) {
        this.subject = Objects.requireNonNull(subject, "subject can't be null!!");
        this.body = Objects.requireNonNull(body, "body can't be null!!");
        this.recipientToList = recipientToList;
    }

    public List<String> getRecipientToList() {
        return recipientToList;
    }

    public List<String> getRecipientCcList() {
        return recipientCcList;
    }

    public List<String> getRecipientBccList() {
        return recipientBccList;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for creating {@link EmailRequest} fluently.
     */
    public static class Builder {

        private List<String> recipientToList;

        private List<String> recipientCcList;

        private List<String> recipientBccList;

        private String subject;

        private String body;

        private Builder() {
        }

        public Builder addRecipientTo(String recipientTo) {
            if (this.recipientToList == null) {
                this.recipientToList = new ArrayList<>();
            }
            this.recipientToList.add(recipientTo);
            return this;
        }

        public Builder addRecipientToList(List<String> recipientToList) {
            if (this.recipientToList == null) {
                this.recipientToList = new ArrayList<>();
            }
            this.recipientToList.addAll(recipientToList);
            return this;
        }

        public Builder addRecipientCc(String recipientCc) {
            if (this.recipientCcList == null) {
                this.recipientCcList = new ArrayList<>();
            }
            this.recipientCcList.add(recipientCc);
            return this;
        }

        public Builder addRecipientCcList(List<String> recipientCcList) {
            if (this.recipientCcList == null) {
                this.recipientCcList = new ArrayList<>();
            }
            this.recipientCcList.addAll(recipientCcList);
            return this;
        }

        public Builder addRecipientBcc(String recipientBcc) {
            if (this.recipientBccList == null) {
                this.recipientBccList = new ArrayList<>();
            }
            this.recipientBccList.add(recipientBcc);
            return this;
        }

        public Builder addRecipientBccList(List<String> recipientBccList) {
            if (this.recipientBccList == null) {
                this.recipientBccList = new ArrayList<>();
            }
            this.recipientBccList.addAll(recipientBccList);
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public EmailRequest build() {
            EmailRequest request = new EmailRequest(this.subject, this.body, this.recipientToList);
            request.recipientCcList = this.recipientCcList;
            request.recipientBccList = this.recipientBccList;
            return request;
        }
    }
}
