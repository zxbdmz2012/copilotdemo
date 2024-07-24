package com.github.copilot.pdf.util;


import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.html.simpleparser.HTMLWorker;

import java.io.*;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
@Slf4j
public class HtmlToPdfUtils {




        public static void convertToPdf(InputStream inputStream, OutputStream outputStream) {
            try {
                // 读取 HTML 内容
                String htmlContent = readHtmlContent(inputStream);

                // 创建 PDF 文档
                Document document = new Document();
                PdfWriter writer = PdfWriter.getInstance(document, outputStream);
                document.open();

                // 使用 Flying Saucer 解析 HTML 并生成 PDF
                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocumentFromString(htmlContent);
                renderer.layout();
                renderer.createPDF(outputStream);

                document.close();
            } catch (DocumentException | IOException e) {
                e.printStackTrace();
            }
        }

        private static String readHtmlContent(InputStream inputStream) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            return content.toString();
        }



    /**
     * 读取HTML 流文件，并查询当中的&nbsp;或类似符号直接替换为空格
     *
     * @param inputStream
     * @return
     */
    private static InputStream readInputStrem(InputStream inputStream)
    {
        // 定义一些特殊字符的正则表达式 如：
        String regEx_special = "\\&[a-zA-Z]{1,10};";
        try
        {
            //<1>创建字节数组输出流，用来输出读取到的内容
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //<2>创建缓存大小
            byte[] buffer = new byte[1024]; // 1KB
            //每次读取到内容的长度
            int len = -1;
            //<3>开始读取输入流中的内容
            while ((len = inputStream.read(buffer)) != -1) { //当等于-1说明没有数据可以读取了
                baos.write(buffer, 0, len);   //把读取到的内容写到输出流中
            }
            //<4> 把字节数组转换为字符串
            String content = baos.toString();
            //<5>关闭输入流和输出流
            //            inputStream.close();
            baos.close();
            //            log.info("读取的内容：{}", content);
            //            判断HTML内容是否具有HTML的特殊字符标记
            Pattern compile = Pattern.compile(regEx_special, Pattern.CASE_INSENSITIVE);
            Matcher matcher = compile.matcher(content);
            String replaceAll = matcher.replaceAll("");
            //            log.info("替换后的内容：{}", replaceAll);
            //            将字符串转化为输入流返回
            InputStream stringStream = getStringStream(replaceAll);
            //<6>返回结果
            return stringStream;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log.error("错误信息：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 将一个字符串转化为输入流
     * @param sInputString 字符串
     * @return
     */
    public static InputStream getStringStream(String sInputString)
    {
        if (sInputString != null && !sInputString.trim().equals(""))
        {
            try
            {
                ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(sInputString.getBytes());
                return tInputStringStream;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

}
