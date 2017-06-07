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
