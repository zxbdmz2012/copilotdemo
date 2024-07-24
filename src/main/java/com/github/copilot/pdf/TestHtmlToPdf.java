package com.github.copilot.pdf;

import com.github.copilot.pdf.util.FreeMarkerUtils;
import com.github.copilot.pdf.util.HtmlToPdfUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TestHtmlToPdf
{
    // 模板路径
    public final static String TEMP = "C:\\Users\\Administrator\\Desktop\\JYX_QYZT\\MicroServices\\base\\file\\src\\main\\resources\\templates\\";

    /**
     * 模板所需的数据
     * @return 数据
     */
    public static Map<String, Object> getContent()
    {

        return new HashMap<>();
    }


    public static void main(String[] args) throws IOException
    {
        long startTime = System.currentTimeMillis();
        // 指定模板渲染值并生成html文件至指定位置
        FreeMarkerUtils.genteratorFile(getContent(),
                TEMP,
                "ProcurementContractTemplate",
                TEMP,
                "afterGeneration.html");

        // 需转换的html文件名称
        String htmlFile = "afterGeneration.html";
        // 转换好pdf存储名称
        String pdfFile = "result.pdf";
        // 自定义水印
        String waterMarkText = "JYX";
        // 读取需转换的html文件
        InputStream inputStream = new FileInputStream(TEMP + htmlFile);
        // 写出pdf存储位置
        OutputStream outputStream = new FileOutputStream(TEMP + pdfFile);
        // 微软雅黑在windows系统里的位置如下，linux系统直接拷贝该文件放在linux目录下即可
        // String fontPath = "src/main/resources/font/STHeiti Light.ttc,0";
        // 开始转换html生成pdf文档
        HtmlToPdfUtils.convertToPdf(inputStream, outputStream);
        log.info("转换结束，耗时：{}ms",System.currentTimeMillis()-startTime);
    }
}


