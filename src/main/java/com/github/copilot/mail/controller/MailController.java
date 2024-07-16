package com.github.copilot.mail.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.copilot.mail.model.BaseMessage;
import com.github.copilot.mail.service.MailSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling mail operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/mail")
public class MailController {

    @Autowired
    private MailSendService mailSendService;

    /**
     * General email sending interface.
     *
     * @param baseMessage The base message to be sent.
     * @return A response indicating the result of the preposition operation.
     */
    @PostMapping(value = "/general/send", produces = "application/json")
    @ResponseBody
    public String generalSend(@RequestBody BaseMessage baseMessage) {
        log.info("[General Request] Starting mail send request, original request parameters: {}", JSONObject.toJSONString(baseMessage));
        return mailSendService.sendEmail(baseMessage);
    }

}