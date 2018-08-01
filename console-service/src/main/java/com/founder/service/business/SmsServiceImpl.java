package com.founder.service.business;

import com.alibaba.fastjson.JSONObject;
import com.founder.contract.business.SmsService;
import com.founder.contract.sysadmin.DictionaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private RestTemplate restTemplate;

//    private HttpPost httpPost = null;
//
//    public synchronized HttpPost getHttpPost() {
//        if (httpPost == null) {
//            httpPost = new HttpPost(dictionaryService.getDictionaryMapByTypeAndDtKey("SMS", "url"));
//            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
//        }
//        return httpPost;
//    }

    /**
     * 发送短息
     *
     * @param phoneNo 收信人号码
     * @param text    短息内容
     */
    @Override
    public void sendSms(String phoneNo, String text) {
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        httpPost = getHttpPost();
//        //拼接参数
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("lx", dictionaryService.getDictionaryMapByTypeAndDtKey("SMS", "type")));//发送类型
//        params.add(new BasicNameValuePair("DLZH", dictionaryService.getDictionaryMapByTypeAndDtKey("SMS", "userName")));//接口认证的用户名
//        params.add(new BasicNameValuePair("DLMM", dictionaryService.getDictionaryMapByTypeAndDtKey("SMS", "password")));//接口认证的密码
//        params.add(new BasicNameValuePair("SJHM", phoneNo));//收信人号码
//        params.add(new BasicNameValuePair("XXNR", text));//短息内容
//        CloseableHttpResponse response = null;
//        try {
//            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
//            response = httpclient.execute(httpPost);
//            HttpEntity entity = response.getEntity();
//            String temp = EntityUtils.toString(entity, "UTF-8");
//            EntityUtils.consume(entity);
//        } catch (Exception e) {
//            throw new OperationException(BusinessError.SmsMessageError);
//        }


        String url = dictionaryService.getDictionaryMapByTypeAndDtKey("SMS", "url");
//        JSONObject postData = new JSONObject();
//        postData.put("lx", dictionaryService.getDictionaryMapByTypeAndDtKey("SMS", "type"));//发送类型
//        postData.put("DLZH", dictionaryService.getDictionaryMapByTypeAndDtKey("SMS", "userName"));//接口认证的用户名
//        postData.put("DLMM", dictionaryService.getDictionaryMapByTypeAndDtKey("SMS", "password"));//接口认证的密码
//        postData.put("SJHM", phoneNo);//收信人号码
//        postData.put("XXNR", text);//短息内容

//        JSONObject json = restTemplate.postForEntity(url, postData, JSONObject.class).getBody();
//        log.info("调用短信接口返回信息{}", json.toJSONString());

        MultiValueMap<String, Object> postData = new LinkedMultiValueMap<String, Object>();
        postData.add("lx", dictionaryService.getDictionaryMapByTypeAndDtKey("SMS", "type"));//发送类型
        postData.add("DLZH", dictionaryService.getDictionaryMapByTypeAndDtKey("SMS", "userName"));//接口认证的用户名
        postData.add("DLMM", dictionaryService.getDictionaryMapByTypeAndDtKey("SMS", "password"));//接口认证的密码
        postData.add("SJHM", phoneNo);//收信人号码
        postData.add("XXNR", text);//短息内容
        String rspData = restTemplate.postForObject(url, postData,String.class);
        log.info("调用短信接口返回信息:{}", rspData);
    }
}
