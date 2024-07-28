package com.github.copilot.pdf.util;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

import java.io.*;
import java.util.Map;

public class FreeMarkerUtils
{
    /**
     * Retrieves a FreeMarker template
     * @param template_path Path to the template directory
     * @param templateFileName Name of the template file
     * @return Template object
     */
    private static Template getTemplate(String template_path, String templateFileName)
    {
        Configuration configuration = new Configuration();
        Template template = null;
        try {
            // Set the directory for template loading
            configuration.setDirectoryForTemplateLoading(new File(template_path));
            // Set the object wrapper
            configuration.setObjectWrapper(new DefaultObjectWrapper());
            // Set the default encoding
            configuration.setDefaultEncoding("UTF-8");
            // Get the template
            template = configuration.getTemplate(templateFileName + ".ftl");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return template;
    }

    /**
     * Generates a file from a FreeMarker template
     * @param input Data map for the template
     * @param template_path Path to the template directory
     * @param templateFileName Name of the template file
     * @param savePath Path to save the generated file
     * @param fileName Name of the generated file
     */
    public static void genteratorFile(Map<String, Object> input, String template_path, String templateFileName, String savePath, String fileName)
    {
        try{

        Configuration configuration = new Configuration(Configuration.VERSION_2_3_0);
        configuration.setClassForTemplateLoading(FreeMarkerUtils.class, "/");
        // Get the template
        Template template = configuration.getTemplate(template_path + templateFileName + ".ftl");
        // Create the save directory if it does not exist
        File filePath = new File(savePath);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        // Create the file path
        String filename = savePath + "\\" + fileName;
        File file = new File(filename);
        // Delete the file if it already exists
        if (!file.exists()) {
            file.delete();
        }
        Writer writer = null;
            // Create a writer with UTF-8 encoding
            writer = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8");
            // Process the template with the input data
            template.process(input, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}