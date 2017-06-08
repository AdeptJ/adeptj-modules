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
package com.adeptj.modules.commons.aws.messaging;

import java.util.Map;

/**
 * API for sending Email or SMS.
 *
 * @author Rakesh.Kumar, AdeptJ.
 */
public interface AWSMessagingService {

    /**
     * Sends the given message(either EMAIL or SMS)
     *
     * @param type {@link MessageType} either EMAIL or SMS
     * @param data Mesage data required by the system
     * @return a status if the Message has been sent or not.
     */
    void sendMessage(MessageType type, Map<String, String> data);
}
