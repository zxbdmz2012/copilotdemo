package com.github.copilot.mail.service;

import com.github.copilot.mail.model.BaseMessage;
import com.github.copilot.mail.util.FreemarkerConfigUtil;
import com.github.copilot.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for sending emails.
 */
@Slf4j
@Service
public class MailSendService {


    // Reading parameter from the configuration file
    @Value("${spring.mail.username:'test'}")
    private String from;

    @Autowired
    private JavaMailSender mailSender;

    public String sendEmail(BaseMessage email) {
        if (email == null || StringUtil.isEmpty(email.getTo())) {
            log.error("Email or recipient address is null or empty.");
            return "Email or recipient address is null or empty.";
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            // Determine if there are attachments to decide on multipart mode
            boolean hasAttachments = StringUtil.isNotEmpty(email.getFilePath());
            MimeMessageHelper helper = new MimeMessageHelper(message, hasAttachments);

            if (StringUtil.isEmpty(email.getFrom())) {
                helper.setFrom(from);
            }else {
                helper.setFrom(email.getFrom());
            }
            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            if (StringUtil.isEmpty(email.getText())) {
                // Inside the sendEmail method, before setting the text for the email
                if (StringUtil.isNotEmpty(email.getTemplateName())) {
                    // Assuming you have a method to populate the model based on the email object
                    // This is a placeholder, you'll need to implement the logic to populate the model
                    populateModelFromEmail(email);
                }
            }
            helper.setText(email.getText(), true);


            if (StringUtil.isNotEmpty(email.getCc())) {
                helper.setCc(email.getCc());
            }

            // Handle attachments
            if (hasAttachments) {
                FileSystemResource file = new FileSystemResource(email.getFilePath());
                helper.addAttachment(email.getAttachName(), file);
            }

            mailSender.send(message);
            return "Email" + (hasAttachments ? " with attachments" : "") + " sent successfully.";
        } catch (Exception e) {
            log.error("Exception in sending email" + (StringUtil.isNotEmpty(email.getFilePath()) ? " with attachments: " : ": "), e.getMessage(), e);
            return "Failed to send email" + (StringUtil.isNotEmpty(email.getFilePath()) ? " with attachments: " : ": ") + e.getMessage();
        }
    }

    private void populateModelFromEmail(BaseMessage email) {
        if (StringUtil.isEmpty(email.getTemplateName())) {
            log.error("Template name is empty.");
            return;
        }

        // Assuming the template name directly maps to a .ftl file under /resources/templates/freemarker
        String templatePath = "/template/freemarker/" + email.getTemplateName() + ".ftl";

        Map<String, Object> model = email.getModel();
        // Use FreemarkerConfigUtil to generate the HTML content
        try {
            String htmlContent = FreemarkerConfigUtil.getTemplate(model, templatePath); // Adjusted method signature for demonstration
            email.setText(htmlContent);
        } catch (Exception e) {
            log.error("Failed to generate email content from template: {}", e.getMessage(), e);
        }
    }
}