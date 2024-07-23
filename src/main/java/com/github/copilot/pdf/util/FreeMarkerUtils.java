package com.github.copilot.pdf.util;


import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

import java.io.*;
import java.util.Map;

public class FreeMarkerUtils
{

    private static Template getTemplate(String template_path, String templateFileName)
    {
        Configuration configuration = new Configuration();
        Template template = null;
        try {
            configuration.setDirectoryForTemplateLoading(new File(template_path));
            configuration.setObjectWrapper(new DefaultObjectWrapper());
            configuration.setDefaultEncoding("UTF-8");   //设置编码格式
            //模板文件
            template = configuration.getTemplate(templateFileName + ".ftl");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return template;
    }

    public static void genteratorFile(Map<String, Object> input, String template_path, String templateFileName, String savePath, String fileName)
    {
        Template template = getTemplate(template_path, templateFileName);
        File filePath = new File(savePath);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        String filename = savePath + "\\" + fileName;
        File file = new File(filename);
        if (!file.exists()) {
            file.delete();
        }
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8");
            template.process(input, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String genterator(Map<String, Object> variables, String template_path, String templateFileName)  throws Exception
    {
        Template template = getTemplate(template_path, templateFileName);
        StringWriter stringWriter = new StringWriter();
        BufferedWriter writer = new BufferedWriter(stringWriter);
        template.setEncoding("UTF-8");
        template.process(variables, writer);
        String htmlStr = stringWriter.toString();
        writer.flush();
        writer.close();
        return htmlStr;
    }
}
