package com.founder.console.web.controller;

import com.founder.Exception.OperationException;
import com.founder.console.web.config.annotation.AjaxController;
import com.founder.console.web.utils.CalendarUtils;
import com.founder.contract.sysadmin.DictionaryService;
import com.founder.dto.business.BusinessValueDto;
import com.founder.dto.commons.ImgBase64Dto;
import com.founder.exception.business.BusinessError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.io.File.separator;

/**
 * Created by luoxi on 2017/7/27.
 */
@AjaxController
@RequestMapping("/commons")
@Slf4j
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
            log.error("上传异常", e);
            map.put("success", "false");
        }

        return map;
    }

    @RequestMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam(required = false) String filePath, HttpServletRequest request) {
        String uploadFilePath = dictionaryService.getDictionaryMapByTypeAndDtKey("UPLOAD_FILE_PATH", "UPLOAD_FILE_PATH");

        byte[] data = null;
        HttpHeaders header = new HttpHeaders();

        try {

            if(StringUtils.isBlank(filePath)){
                log.error("文件路径为空");
                throw new OperationException(BusinessError.DownloadError);
            }

            filePath = filePath.replace("..","");

            if(!this.match("^(\\/|\\\\)\\d{6}(\\/|\\\\).*",filePath)){
                log.error("文件路径错误");
                throw new OperationException(BusinessError.DownloadError);
            }

            String path = filePath.substring(1, filePath.length());
            String absFilePath = uploadFilePath + path;


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
        } catch (OperationException e) {
            log.error("下载异常", e);
            throw e;
        }catch (Exception e) {
            log.error("下载异常", e);

        }
        return new ResponseEntity<byte[]>(data, header, HttpStatus.OK);
    }

    @RequestMapping("/uploadImgBase64")
    @ResponseBody
    public Map<String, Object> uploadImgBase64(@RequestBody ImgBase64Dto imgBase64Dto) {

        String fileName = imgBase64Dto.getFileName();
        String imgBase64 = imgBase64Dto.getImgBase64();

        Map<String, Object> map = new HashMap<String, Object>();

        //String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();//上传的文件的后缀

        String systemTime = CalendarUtils.parsefomatCalendar(Calendar.getInstance(), "yyyyMMddHHmmss");

        String newFileName = systemTime+"_"+fileName;//上传后的文件名字

        String yearAndMonth = CalendarUtils.parsefomatCalendar(Calendar.getInstance(), CalendarUtils.SHORT_FORMAT).substring(0, 6);



        String uploadFilePath = dictionaryService.getDictionaryMapByTypeAndDtKey("UPLOAD_FILE_PATH", "UPLOAD_FILE_PATH");

        String filePath = uploadFilePath+yearAndMonth;

        String absFilePath = uploadFilePath + yearAndMonth + separator + newFileName;

        String relFilePath = separator + yearAndMonth + separator + newFileName;


        BASE64Decoder decoder = new BASE64Decoder();

        FileOutputStream fos = null;
        try {

            File file = new File(filePath);

            if(!file.exists()){
                file.mkdirs();
            }


            //imgBase64 = URLDecoder.decode(imgBase64,"UTF-8");
            byte[] decodedBytes = decoder.decodeBuffer(imgBase64);// 将字符串格式的imagedata转为二进制流（biye[])的decodedBytes


            fos = new FileOutputStream(absFilePath);
            //将字节数组bytes中的数据，写入文件输出流fos中
            fos.write(decodedBytes);
            fos.flush();

            map.put("path", relFilePath);
            map.put("fileName",newFileName);
            map.put("success", "true");
        } catch (IOException e) {
            log.error("上传图片异常", e);
            map.put("success", Boolean.FALSE);
        }finally {
            try {
                if(fos!=null){
                    fos.close();
                }
            } catch (IOException e) {
                log.error("关闭IO流异常", e);
            }
        }
        return map;
    }

    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static void main(String[] arg){

        System.out.println(CommonController.match("^(\\/|\\\\)\\d{6}(\\/|\\\\).*","/201801/sfdsfsdf"));
        System.out.println(CommonController.match("^(\\/|\\\\)\\d{6}(\\/|\\\\).*","\\201801\\345435345"));
        System.out.println(CommonController.match("^(\\/|\\\\)\\d{6}(\\/|\\\\).*","\201801222"));
        System.out.println(CommonController.match("^(\\/|\\\\)\\d{6}(\\/|\\\\).*","a2018012\222"));
        System.out.println(CommonController.match("^(\\/|\\\\)\\d{6}(\\/|\\\\).*","\201801a\222"));
    }
}
