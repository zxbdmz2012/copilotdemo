package com.github.copilot.mail.service;

import com.github.copilot.mail.model.BaseMessage;
import com.github.copilot.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.mail.internet.MimeMessage;

/**
 * Service for sending emails.
 */
@Slf4j
@Service
public class MailSendService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sends a simple email.
     *
     * @param email The email message to be sent.
     * @return A string indicating the result of the operation.
     */
    public String sendMail(BaseMessage email) {
        if (email == null || StringUtil.isEmpty(email.getTo())) {
            log.error("Email or recipient address is null or empty.");
            return "Email or recipient address is null or empty.";
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(email.getFrom());
            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            // HTML text needs true to take effect
            helper.setText(email.getText(), true);
            if (StringUtil.isNotEmpty(email.getCc())) {
                helper.setCc(email.getCc());
            }
            mailSender.send(message);
            return "Email sent successfully.";
        } catch (Exception e) {
            log.error("Exception in sending simple email: {}", e.getMessage(), e);
            return "Failed to send email: " + e.getMessage();
        }
    }

    /**
     * Sends an email with attachments.
     *
     * @param email The email message to be sent.
     * @return A string indicating the result of the operation.
     */
    public String sendAttachmentsMail(BaseMessage email) {
        if (email == null || StringUtil.isEmpty(email.getTo()) || StringUtil.isEmpty(email.getFilePath())) {
            log.error("Email, recipient address, or file path is null or empty.");
            return "Email, recipient address, or file path is null or empty.";
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(email.getFrom());
            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            helper.setText(email.getText());
            if (StringUtil.isNotEmpty(email.getCc())) {
                helper.setCc(email.getCc());
            }

            FileSystemResource file = new FileSystemResource(email.getFilePath());
            helper.addAttachment(email.getAttachName(), file);
            mailSender.send(message);
            return "Email with attachments sent successfully.";
        } catch (Exception e) {
            log.error("Exception in sending email with attachments: {}", e.getMessage(), e);
            return "Failed to send email with attachments: " + e.getMessage();
        }
    }
}