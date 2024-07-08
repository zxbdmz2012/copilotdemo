package com.github.copilot.mail.service;


import com.github.copilot.mail.model.BaseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;


@Slf4j
@Service
public class MailSendService {

    @Autowired
    private JavaMailSender mailSender;


    /**
     * @Description: 发送普通邮件
     */
    public String sendMail(BaseMessage email) {
        MimeMessage message = null;
        try {
            message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(email.getFrom());
            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            // 发送htmltext值需要给个true，不然不生效
            helper.setText(email.getText(), true);
            if (email.getCc() != null) {
                helper.setCc(email.getCc());
            }
            mailSender.send(message);
            return ("发送成功");
        } catch (Exception e) {
            log.error("发送普通邮件异常！{}", e.getMessage(), e);
            return (e.getMessage());
        }

    }

    /**
     * @Description: 发送带附件的邮件
     * @author lc
     */
    public String sendAttachmentsMail(BaseMessage email) {
        MimeMessage message = null;
        try {
            message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(email.getFrom());
            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            helper.setText(email.getText());
            if (email.getCc() != null) {
                helper.setCc(email.getCc());
            }

            FileSystemResource fileSystemResource = new FileSystemResource(email.getFilePath());
            helper.addAttachment(email.getAttachName(), fileSystemResource);
            mailSender.send(message);
            return ("发送成功");
        } catch (Exception e) {
            log.error("发送附件邮件异常！{}", e.getMessage(), e);
            return (e.getMessage());
        }
    }
}