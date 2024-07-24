package com.github.copilot.pdf.util;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class HtmlAttributeWrapper {

    // List of attributes to be wrapped in quotes
    private static final List<String> attributesToWrap = Arrays.asList("width", "border");

    public static void main(String[] args) {
        String inputFilePath = "path/to/input.html";
        String outputFilePath = "path/to/output.html";

        try {
            // Read the HTML file content
            String content = new String(Files.readAllBytes(Paths.get(inputFilePath)));

            // Process the content to wrap attributes in quotes
            String modifiedContent = wrapAttributesInQuotes(content, attributesToWrap);

            // Write the modified content to a new HTML file
            Files.write(Paths.get(outputFilePath), modifiedContent.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String wrapAttributesInQuotes(String content, List<String> attributes) {
        for (String attribute : attributes) {
            // Regular expression to find attributes without quotes
            String regex = attribute + "=([^\"\\s>]+)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(content);

            // Replace the attributes with their values wrapped in quotes
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sb, attribute + "=\"" + matcher.group(1) + "\"");
            }
            matcher.appendTail(sb);
            content = sb.toString();
        }
        return content;
    }
}