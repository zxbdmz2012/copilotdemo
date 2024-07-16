package com.github.copilot.mail.util;


import com.github.copilot.exceptionhandler.exception.category.BizException;
import com.github.copilot.exceptionhandler.exception.error.CommonErrorCode;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.Map;

@Slf4j
public class FreemarkerConfigUtil {

    public static String getTemplate(Map<String, Object> model, String templatePath) {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_0);
        cfg.setClassForTemplateLoading(FreemarkerConfigUtil.class, "/");
        try {
            Template template = cfg.getTemplate(templatePath);
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (Exception e) {
            log.error("Failed to get the template: {}", e.getMessage(), e);
        }
        throw new BizException(CommonErrorCode.EXCEPTION.getCode(), "Failed to get the template.");
    }

}
