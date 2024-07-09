package com.github.copilot.mail.service;

import com.github.copilot.mail.model.AttachFlag;
import com.github.copilot.mail.model.BaseMessage;
import com.github.copilot.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for managing mail operations.
 */
@Slf4j
@Service
public class MailManageService {

    // Reading parameter from the configuration file
    @Value("${spring.mail.username:'test'}")
    private String from;

    @Autowired
    private MailSendService sendService;

    /**
     * Preposition operation for emails.
     *
     * @param message The base message to be processed.
     * @return A string indicating the result of the operation.
     */
    public String preposition(BaseMessage message) {
        // Set the sender's email if not specified
        if (StringUtil.isEmpty(message.getFrom())) {
            message.setFrom(from);
        }

        // Determine the type of email to send based on attachment flag
        if (AttachFlag.N.name().equalsIgnoreCase(message.getAttachFlag())) {
            return sendService.sendMail(message);
        } else if (AttachFlag.Y.name().equalsIgnoreCase(message.getAttachFlag())) {
            return sendService.sendAttachmentsMail(message);
        } else {
            return "Attachment flag error!";
        }
    }

}