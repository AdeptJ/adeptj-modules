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

package com.adeptj.modules.commons.email;

import jakarta.mail.internet.MimeMultipart;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ProviderType;

import java.util.function.Supplier;

/**
 * Service interface for sending emails using Java mails/Apache commons email APIs.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
@ProviderType
public interface EmailService {

    void sendSimpleEmail(@NotNull EmailInfo emailInfo);

    void sendHtmlEmail(@NotNull EmailInfo emailInfo);

    void sentMultipartEmail(@NotNull EmailInfo emailInfo, @NotNull Supplier<MimeMultipart> supplier);
}
