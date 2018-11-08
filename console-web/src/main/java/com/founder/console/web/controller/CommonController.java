package com.founder.console.web.controller;

import com.founder.console.web.config.annotation.AjaxController;
import com.founder.console.web.utils.CalendarUtils;
import com.founder.contract.sysadmin.DictionaryService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static java.io.File.separator;

/**
 * Created by luoxi on 2017/7/27.
 */
@AjaxController
@RequestMapping("/commons")
public class CommonController {


    @Autowired
    private DictionaryService dictionaryService;

    @RequestMapping("/upload")
    @ResponseBody
    public Map<String, String> upload(@RequestPart("file") MultipartFile file) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            //一次遍历所有文件
            if (file != null) {
                String yearAndMonth = CalendarUtils.parsefomatCalendar(Calendar.getInstance(), CalendarUtils.SHORT_FORMAT).substring(0, 6);

                String systemTime = CalendarUtils.parsefomatCalendar(Calendar.getInstance(), "yyyyMMddHHmmss");

                String uploadFilePath = dictionaryService.getDictionaryMapByTypeAndDtKey("UPLOAD_FILE_PATH", "UPLOAD_FILE_PATH");

                String absFilePath = uploadFilePath + yearAndMonth + separator + systemTime + "_" + file.getOriginalFilename();

                String relFilePath = separator + yearAndMonth + separator + systemTime + "_" + file.getOriginalFilename();

                File fileDir = new File(absFilePath);
                if (!fileDir.exists()) { //如果不存在 则创建
                    fileDir.mkdirs();
                }
                //上传
                file.transferTo(new File(absFilePath));

                map.put("path", relFilePath);
                map.put("fileName", file.getOriginalFilename());

            }
            map.put("success", "true");

        } catch (Exception e) {
            map.put("success", "false");
        }

        return map;
    }

    @RequestMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam(required = false) String filePath, HttpServletRequest request) {
        String uploadFilePath = dictionaryService.getDictionaryMapByTypeAndDtKey("UPLOAD_FILE_PATH", "UPLOAD_FILE_PATH");
        String path = filePath.substring(1, filePath.length());
        String absFilePath = uploadFilePath + path;
        byte[] data = null;
        HttpHeaders header = new HttpHeaders();
        try {
            File file = new File(absFilePath);
            String fileName = "";
            String httpHeader = request.getHeader("User-Agent").toUpperCase();
            if (httpHeader.contains("MSIE") || httpHeader.contains("TRIDENT") || httpHeader.contains("EDGE")) {
                fileName = URLEncoder.encode(file.getName(), "utf-8");
                fileName = fileName.replace("+", "%20");    //IE下载文件名空格变+号问题
            } else {
                fileName = new String(file.getName().getBytes("utf-8"), "ISO8859-1");
            }
            header.setContentType(MediaType.parseMediaType("application/x-msdownload"));
            header.set("Content-Disposition", "attachment; filename=" + fileName);
            data = FileUtils.readFileToByteArray(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<byte[]>(data, header, HttpStatus.OK);
    }

    @RequestMapping("/uploadImgBase64")
    @ResponseBody
    public Map<String, String> uploadImgBase64(@RequestParam(required = false) String imgBase64, @RequestParam(required = false) String fileName) {


        Map<String, String> map = new HashMap<String, String>();

        String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();//上传的文件的后缀

        String newFileName = fileName+ "." + fileExt;//上传后的文件名字

        String yearAndMonth = CalendarUtils.parsefomatCalendar(Calendar.getInstance(), CalendarUtils.SHORT_FORMAT).substring(0, 6);

        String systemTime = CalendarUtils.parsefomatCalendar(Calendar.getInstance(), "yyyyMMddHHmmss");

        String uploadFilePath = dictionaryService.getDictionaryMapByTypeAndDtKey("UPLOAD_FILE_PATH", "UPLOAD_FILE_PATH");

        String absFilePath = uploadFilePath + yearAndMonth + separator + systemTime + "_" +newFileName;

        String relFilePath = separator + yearAndMonth + separator + systemTime + "_" + newFileName;




        imgBase64 = imgBase64.substring(30);

        FileOutputStream fos = null;
        try {
            imgBase64 = URLDecoder.decode(imgBase64,"UTF-8");
            byte[] decodedBytes = Base64.getDecoder().decode(imgBase64);// 将字符串格式的imagedata转为二进制流（biye[])的decodedBytes
            for(int i=0;i<decodedBytes.length;++i){
                if(decodedBytes[i]<0) {
                    //调整异常数据
                    decodedBytes[i]+=256;
                }
            }

            fos = new FileOutputStream(absFilePath);
            //将字节数组bytes中的数据，写入文件输出流fos中
            fos.write(decodedBytes);
            fos.flush();

            map.put("path", relFilePath);
            map.put("fileName",newFileName);
            map.put("success", "true");
        } catch (IOException e) {
            e.printStackTrace();
            map.put("success", "false");
        }finally {
            try {
                if(fos!=null){
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
