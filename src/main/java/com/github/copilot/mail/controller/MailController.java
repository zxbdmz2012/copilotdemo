package com.github.copilot.mail.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.copilot.mail.model.BaseMessage;
import com.github.copilot.mail.service.MailSendService;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequestMapping("/api/mail")
@Tag(name = "MailController", description = "Controller for handling mail operations.")
public class MailController {

    @Autowired
    private MailSendService mailSendService;

    @Operation(summary = "Send a general email", description = "General email sending interface.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email sent successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request")
            })
    @PostMapping(value = "/general/send", produces = "application/json")
    @ResponseBody
    public String generalSend(@RequestBody BaseMessage baseMessage) {
        log.info("[General Request] Starting mail send request, original request parameters: {}", JSONObject.toJSONString(baseMessage));
        return mailSendService.sendEmail(baseMessage);
    }

}