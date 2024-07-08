package com.github.copilot.mail.controller;


import com.alibaba.fastjson.JSONObject;
import com.github.copilot.mail.model.BaseMessage;
import com.github.copilot.mail.service.MailManageService;
import com.github.copilot.mail.service.MailSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/mail")
public class MailController {

    @Autowired
    private MailManageService manageService;
    @Autowired
    private MailSendService sendService;

    /**
     * @Description: 通用发送邮件接口
     */
    @RequestMapping(value = "/general/send", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String generalSend(@RequestBody BaseMessage baseMessage) {
        log.info("[通用请求]开始请求发送邮件, 业务原始请求参数为: {}", JSONObject.toJSONString(baseMessage));
        return manageService.preposition(baseMessage);
    }

}