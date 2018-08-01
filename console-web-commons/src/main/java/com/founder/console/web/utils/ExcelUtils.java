package com.founder.console.web.utils;

import com.founder.contract.sysadmin.DictionaryService;
import com.founder.dto.commons.ExcelSheetDto;
import jxl.SheetSettings;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ExcelUtils {

    @Autowired
    private DictionaryService dictionaryService;

    public byte[] exportExcel(String fileName, String[] heads, List<Map<String, String>> lists) {
        byte[] data = null;
        HttpHeaders header = new HttpHeaders();
        try {
            Map<String, Object> condition = new HashMap<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYYMMDDhhmmss");

            WritableWorkbook workbook;
            String absolutePath = dictionaryService.getDictionaryMapByTypeAndDtKey("EXPORT_FILE", "ABSOLUTE_PATH");
            if (null == absolutePath || "".equals(absolutePath)) {
                absolutePath = "./";
            }
            String folder = dictionaryService.getDictionaryMapByTypeAndDtKey("EXPORT_FILE", "FOLDER");
            if (null == folder || "".equals(folder)) {
                folder = "./";
            }
//            absolutePath = "c://work";
//            folder = "test";
            String path = folder + File.separator + fileName + simpleDateFormat.format(new Date());

            File dir = new File(absolutePath + folder);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(absolutePath + path);
            workbook = jxl.Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet(fileName, 0);
            WritableCellFormat writableCellFormat = new WritableCellFormat();
            writableCellFormat.setAlignment(Alignment.CENTRE);

            for (int i = 0; i < heads.length; i++) {
                sheet.addCell(new Label(i, 0, heads[i]));
            }

            //表头占一行,所以row的j+1开始
            if (lists != null && lists.size() > 0) {
                for (int j = 0; j < lists.size(); j++) {
                    for (int k = 0; k < lists.get(j).size(); k++) {
                        sheet.addCell(new Label(k, j + 1, lists.get(j).get("C" + k) == null ? "" : lists.get(j).get("C" + k), writableCellFormat));
                    }
                }
            }

            workbook.write();
            workbook.close();
            data = FileUtils.readFileToByteArray(new File(absolutePath + path));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

//    private List<Map<String,String>> convertDataList(String className,List<?> resultList) throws Exception {
//        Class<?> classInList = null;
//        Map<String,String> content = new HashMap<>();
//
//        classInList = Class.forName(className);
//        Field[] fileds = classInList.getFields();
//        for (int j=0;j<resultList.size();j++) {
//            Object obj = resultList.get(j) ;
//            for (int i = 0; i < fileds.length; i++) {
//                Method method = classInList.getMethod("get" + fileds);
//                String context = method.invoke(classInList.newInstance()).toString();
//                content.put("C" + i, context);
//            }
//        }
//
//
//        return null;
//    }


    public HttpServletResponse exportExcel(String fileName, String sheetName, List<String> tableHeader, List<Object> dataList, HttpServletResponse httpServletResponse) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String defaultfileName = sdf.format(new Date()) + ".xls";
        if ("".equals(fileName) || null == fileName) {
            fileName = defaultfileName;
        }
        httpServletResponse.setContentType("application/x-excel");
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.addHeader("Content-Disposition", "attachment;filename=" + fileName);

        // 业务数据，不用关注，网友请假装有数据，哈哈哈...下同
        List<?> itemList = new ArrayList<>();

        try {
            // 1.创建excel文件
            WritableWorkbook book = Workbook.createWorkbook(httpServletResponse.getOutputStream());
            // 居中
            WritableCellFormat wf = new WritableCellFormat();
            wf.setAlignment(Alignment.CENTRE);

            WritableSheet sheet = null;
            SheetSettings settings = null;
            for (int i = 0; i < itemList.size(); i++) {
                //设置sheet名称
                sheet = book.createSheet(sheetName, i);
                settings = sheet.getSettings();
                settings.setVerticalFreeze(1);
                //设置表头
                for (String header : tableHeader) {
                    int cellNo = 0;
                    sheet.addCell(new Label(cellNo, 0, header, wf));
                    cellNo++;
                }
                //填写内容
                List<?> hisList = new ArrayList<>();
                if (hisList != null && hisList.size() > 0) {
                    for (int j = 0; j < hisList.size(); j++) {
                        sheet.addCell(new Label(0, j + 2, hisList.get(j).toString() + "", wf));
                        sheet.addCell(new Label(1, j + 2, hisList.get(j).toString() + "", wf));
                    }
                }
            }
            // 6.写入excel并关闭
            book.write();
            book.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return httpServletResponse;
    }


    public byte[] exportExcel(String fileName, Map<ExcelSheetDto, List<Map<String, String>>> dataMap) {
        byte[] data = null;
        HttpHeaders header = new HttpHeaders();
        try {
            Map<String, Object> condition = new HashMap<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYYMMDDhhmmss");

            WritableWorkbook workbook;
            String absolutePath = dictionaryService.getDictionaryMapByTypeAndDtKey("EXPORT_FILE", "ABSOLUTE_PATH");
            if (null == absolutePath || "".equals(absolutePath)) {
                absolutePath = "./";
            }
            String folder = dictionaryService.getDictionaryMapByTypeAndDtKey("EXPORT_FILE", "FOLDER");
            if (null == folder || "".equals(folder)) {
                folder = "./";
            }
//            absolutePath = "c://work";
//            folder = "test";
            String path = folder + File.separator + fileName + simpleDateFormat.format(new Date());

            File dir = new File(absolutePath + folder);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(absolutePath + path);
            workbook = jxl.Workbook.createWorkbook(file);
            WritableCellFormat writableCellFormat = new WritableCellFormat();
            writableCellFormat.setAlignment(Alignment.CENTRE);

            for (ExcelSheetDto excelSheetDto : dataMap.keySet()) {

                WritableSheet sheet = workbook.createSheet(excelSheetDto.getSheetName(), 0);

                for (int i = 0; i < excelSheetDto.getHeads().length; i++) {
                    sheet.addCell(new Label(i, 0, excelSheetDto.getHeads()[i]));
                }

                //表头占一行,所以row的j+1开始
                if (dataMap.get(excelSheetDto) != null && dataMap.get(excelSheetDto).size() > 0) {
                    for (int j = 0; j < dataMap.get(excelSheetDto).size(); j++) {
                        for (int k = 0; k < dataMap.get(excelSheetDto).get(j).size(); k++) {
//                            sheet.addCell(new Label(k, j+1, dataMap.get(excelSheetDto).get(j).get("C"+k) + "", writableCellFormat));
                            sheet.addCell(new Label(k, j + 1, dataMap.get(excelSheetDto).get(j).get("C" + k) == null ? "" : dataMap.get(excelSheetDto).get(j).get("C" + k),
                                    writableCellFormat));
                        }
                    }
                }
            }


            workbook.write();
            workbook.close();
            data = FileUtils.readFileToByteArray(new File(absolutePath + path));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public String convertFileNameByBrowser(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {
        String name = "";
        String httpHeader = request.getHeader("User-Agent").toUpperCase();
        if (httpHeader.contains("MSIE") || httpHeader.contains("TRIDENT") || httpHeader.contains("EDGE")) {
            name = URLEncoder.encode(fileName, "utf-8");
            name = name.replace("+", "%20");    //IE下载文件名空格变+号问题
        } else {
            name = new String(fileName.getBytes("utf-8"), "ISO8859-1");
        }
        return name;
    }


}
