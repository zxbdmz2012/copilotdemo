package com.github.copilot.mail.service;


import com.github.copilot.mail.model.AttachFlag;
import com.github.copilot.mail.model.BaseMessage;
import com.github.copilot.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class MailManageService {

    //读取配置文件中的参数
    @Value("${spring.mail.username:'test'}")
    private String from;

    @Autowired
    private MailSendService sendService;

    /**
     * @Description: 邮件前置操作
     */
    public String preposition(BaseMessage message) {
        if (StringUtil.isEmpty(message.getFrom())) {
            message.setFrom(from);
        }

        if (AttachFlag.N.name().equalsIgnoreCase(message.getAttachFlag())) {
            return sendService.sendMail(message);
        } else if (AttachFlag.Y.name().equalsIgnoreCase(message.getAttachFlag())) {
            return sendService.sendAttachmentsMail(message);
        } else {
            return "附件标识错误！";
        }
    }

}