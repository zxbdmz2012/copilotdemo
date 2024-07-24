package com.github.copilot.pdf.util;

import java.io.*;
import java.nio.file.*;
import java.util.regex.*;

public class HtmlStyleRemover {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: HtmlStyleRemover <inputFile> <outputFile>");
            return;
        }

        String inputFilePath = args[0];
        String outputFilePath = args[1];

        try {
            String content = new String(Files.readAllBytes(Paths.get(inputFilePath)));
            String modifiedContent = removeStyleAttributes(content);
            Files.write(Paths.get(outputFilePath), modifiedContent.getBytes());
            System.out.println("Style attributes removed successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String removeStyleAttributes(String html) {
        String regex = "(?i)\\s*style\\s*=\\s*\"[^\"]*\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);
        return matcher.replaceAll("");
    }
}