package com.github.copilot.mail.model;

import lombok.Data;
import java.io.Serializable;
import java.util.Map;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Represents the model for an email message.")
public class BaseMessage implements Serializable {

    @Schema(description = "The sender's email address.", example = "sender@example.com")
    private String from;

    @Schema(description = "The recipient(s) email addresses.", example = "[\"recipient1@example.com\", \"recipient2@example.com\"]")
    private String[] to;

    @Schema(description = "The carbon copy (CC) recipient(s) email addresses.", example = "[\"cc1@example.com\", \"cc2@example.com\"]")
    private String[] cc;

    @Schema(description = "The subject of the email.", example = "Your Subject Here")
    private String subject;

    @Schema(description = "The text content of the email.", example = "Hello, this is the email content.")
    private String text;

    @Schema(description = "Indicates if the email content is HTML (true) or plain text (false).", example = "true")
    private boolean html;

    @Schema(description = "The type of the attachment.", example = "application/pdf")
    private String attachType;

    @Schema(description = "The name of the attachment.", example = "document.pdf")
    private String attachName;

    @Schema(description = "The content of the attachment.", example = "Base64 encoded content")
    private String attachContent;

    @Schema(description = "The file path of the attachment.", example = "/path/to/attachment.pdf")
    private String filePath;

    @Schema(description = "The name of the template used for generating the email content.", example = "templateName")
    private String templateName;

    @Schema(description = "The model data for the template.", example = "{}")
    private Map<String, Object> model;

    public BaseMessage() {
    }
}