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
        // 从数据库中获取数据， 出于演示目的， 这里数据不从数据库获取， 而是直接写死
        Map input = new HashMap();
        input.put("采购单号", "Fuck you！采购单号~");
        input.put("单据日期", "Fuck you！2");
        input.put("工厂编号", "Fuck you！3");
        input.put("供应商名称", "Fuck you！4");
        input.put("工厂地址", "Fuck you！5");
        input.put("供货方地址", "Fuck you！6");
        input.put("公司电话", "Fuck you！7");
        input.put("供应商电话", "Fuck you！8");
        input.put("公司传真", "Fuck you！9");
        input.put("供应商传真", "Fuck you！10");
        input.put("公司联系人", "Fuck you！11");
        input.put("供应商联系人及手机号", "Fuck you！12");
        input.put("结算方式", "Fuck you！13");
        input.put("运费", "Fuck you！14");
        input.put("开机费", "Fuck you！15");
        input.put("备注", "Fuck you！16");
        input.put("合计金额", "Fuck you！17");
        input.put("采购金额", "Fuck you！18");
        // input.put("印章", "display:none");
        input.put("印章", "display:block");
        input.put("制表人", "Fuck you！20");
        // 表格渲染数据
//        List<DuizhangDomain> dzList = new ArrayList<>();
//        dzList.add(new DuizhangDomain(1,"工单号", "物料料号", "物料名称","规格",1.0,"单位",16.5800,16.5800,16.5800, DateUtil.now(),"我的字体太长了怎么办"));
//        dzList.add(new DuizhangDomain(2,"工单号2", "物料料号2", "物料名称2","规格2",2.0,"单位2",16.5800,16.5800,16.5800, DateUtil.now(),"怎么办怎么办怎么办怎么办"));
//        dzList.add(new DuizhangDomain(3,"工单号3", "物料料号3", "物料名称3","规格3",2.0,"单位3",16.5800,16.5800,16.5800, DateUtil.now(),"怎么办怎么办怎么办怎么办~"));
//        dzList.add(new DuizhangDomain(4,"工单号4", "物料料号4", "物料名称4","规格4",2.0,"单位4",16.5800,16.5800,16.5800, DateUtil.now(),"怎么办怎么办怎么办怎么办怎么办怎么办怎么办"));
//        input.put("users", dzList);

        return input;
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
        HtmlToPdfUtils.convertToPdf(inputStream, waterMarkText, null, outputStream);
        log.info("转换结束，耗时：{}ms",System.currentTimeMillis()-startTime);
    }
}


