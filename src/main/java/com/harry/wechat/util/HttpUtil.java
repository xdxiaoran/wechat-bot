package com.harry.wechat.util;

import com.alibaba.fastjson.JSON;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.harry.wechat.config.ServerConfig;
import com.harry.wechat.dto.server.AccountInfo;
import com.harry.wechat.dto.server.Instruction;
import com.harry.wechat.dto.server.LoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Harry
 * @date 2020/10/22
 * Time: 23:41
 * Desc: HttpUtil
 */
@Component
public class HttpUtil {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ServerConfig serverConfig;

    public static final String PROTOCOL = "http://";


    public <T> List<T> postForArray(Instruction instruction, Class<T> tClass) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            //用HttpEntity封装整个请求报文
            HttpEntity<String> httpEntity = new HttpEntity(JSON.toJSON(instruction).toString(), headers);

            String backInfo = getRestTemplateBuilder().postForObject(PROTOCOL + serverConfig.getUrl(), httpEntity, String.class);
            return JSON.parseArray(backInfo, tClass);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }
    }

    public <T> T postForObject(Instruction instruction, Class<T> tClass) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            //用HttpEntity封装整个请求报文
            HttpEntity<String> httpEntity = new HttpEntity(JSON.toJSON(instruction).toString(), headers);

            String backInfo = getRestTemplateBuilder().postForObject(PROTOCOL + serverConfig.getUrl(), httpEntity, String.class);
            return JSON.parseObject(backInfo, tClass);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public RestTemplate getRestTemplateBuilder() {
        List<HttpMessageConverter<?>> httpMessageConverters = restTemplate.getMessageConverters();
        httpMessageConverters.stream().forEach(httpMessageConverter -> {
            if (httpMessageConverter instanceof StringHttpMessageConverter) {
                StringHttpMessageConverter messageConverter = (StringHttpMessageConverter) httpMessageConverter;
                messageConverter.setDefaultCharset(Charset.forName("UTF-8"));
            }
        });
        return restTemplate;
    }
}
