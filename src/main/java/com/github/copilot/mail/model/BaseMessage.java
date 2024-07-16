package com.github.copilot.mail.model;

import lombok.Data;
import java.io.Serializable;
import java.util.Map;

/**
 * Represents the model for an email message.
 */
@Data
public class BaseMessage implements Serializable {

    /**
     * The sender's email address.
     */
    private String from;

    /**
     * The recipient(s) email addresses.
     */
    private String[] to;

    /**
     * The carbon copy (CC) recipient(s) email addresses.
     */
    private String[] cc;

    /**
     * The subject of the email.
     */
    private String subject;

    /**
     * The text content of the email.
     */
    private String text;

    /**
     * Indicates if the email content is HTML (true) or plain text (false).
     */
    private boolean html = true;

    /**
     * The type of the attachment.
     */
    private String attachType;

    /**
     * The name of the attachment.
     */
    private String attachName;

    /**
     * The content of the attachment.
     */
    private String attachContent;

    /**
     * The file path of the attachment.
     */
    private String filePath;

    /**
     * The name of the template used for generating the email content.
     */
    private String templateName;

    /**
     * The model data for the template.
     */
    private Map<String, Object> model;

    /**
     * Default constructor.
     */
    public BaseMessage() {
    }
}