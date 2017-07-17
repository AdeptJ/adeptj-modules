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

package com.adeptj.modules.aws.ses;

import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;

/**
 * JAX-RS Form bean to map send email form element.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public class EmailForm {

    @NotNull
    @FormParam("recipient")
    private String recipient;

    @NotNull
    @FormParam("subject")
    private String subject;

    @NotNull
    @FormParam("body")
    private String body;

    String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
