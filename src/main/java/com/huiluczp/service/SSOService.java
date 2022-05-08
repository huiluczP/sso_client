package com.huiluczp.service;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import javax.servlet.http.HttpSession;

@Service
public class SSOService {

    @Value("${sso.login-page}")
    private String urlLoginPage;

    @Value("${sso.validate}")
    private String urlValidate;

    @Value("${sso.prefix}")
    private String prefix;

    @Value("${sso.logout}")
    private String urlLogout;

    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);
        factory.setConnectTimeout(15000);
        // 设置代理
        //factory.setProxy(null);
        return factory;
    }

    // 通过外部接口获取token可行性
    // 可行直接增加session
    // 不存在则进行跳转
    public void validateTokenFromSSO(HttpServletRequest request, HttpServletResponse response, String token) throws IOException {
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(prefix).
                path(urlValidate).build(true);
        URI url = uriComponents.toUri();

        RestTemplate restTemplate = restTemplate(simpleClientHttpRequestFactory());

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>(2);
        params.add("token", token);
        System.out.println("token " + token + " 已注入");

        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(url, params, JSONObject.class);
        JSONObject body = responseEntity.getBody();

        int statusCodeValue = responseEntity.getStatusCodeValue();
        if(statusCodeValue!=200||body==null){
            System.out.println("验证接口连接失败");
        }else{
            if((boolean)body.get("success")){
                String userJson = (String) body.get("message");

                System.out.println("验证成功:" + userJson);
                userJson = StringEscapeUtils.unescapeJava(userJson);
                System.out.println(userJson.substring(1, userJson.length()-1));

                JSONObject json = JSONObject.parseObject(userJson.substring(1, userJson.length()-1));
                String userName = json.getString("userName");
                String role = json.getString("role");
                addSessionInfo(request, userName, role);
            }else{
                System.out.println(statusCodeValue);
                String userJson = (String) body.get("message");
                System.out.println(userJson);
                redirectToLoginPage(request, response);
            }
        }
    }

    // 跳转到login页面
    // 将原始页面添加到末尾
    @CrossOrigin
    public void redirectToLoginPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("跳转登陆页面");
        String formerUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getRequestURI();
        System.out.println(prefix + urlLoginPage + "?redirect=" + formerUrl);
        response.sendRedirect(prefix + urlLoginPage + "?redirect=" + formerUrl);
    }

    public void addSessionInfo(HttpServletRequest request, String userName, String role){
        HttpSession session = request.getSession();
        session.setAttribute("role", role);
        session.setAttribute("user", userName);
    }

    public void logOutFromSSO(HttpServletRequest request, HttpServletResponse response, String token){
        // 链接sso中心的logout方法，把token删除
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(prefix).
                path(urlLogout).build(true);
        URI url = uriComponents.toUri();

        RestTemplate restTemplate = restTemplate(simpleClientHttpRequestFactory());

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>(2);
        params.add("token", token);
        System.out.println("token " + token + " 已注入");

        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(url, params, JSONObject.class);
        JSONObject body = responseEntity.getBody();

        int statusCodeValue = responseEntity.getStatusCodeValue();
        if(statusCodeValue!=200||body==null){
            System.out.println("验证接口连接失败");
        }else{
            System.out.println(statusCodeValue);
            String message = (String) body.get("message");
            System.out.println(message);
        }
    }
}
