package com.huiluczp.controller;

import com.alibaba.fastjson.JSONObject;
import com.huiluczp.service.SSOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class MainController {

    @Autowired
    SSOService ssoService;

    // main页面
    @RequestMapping("/main")
    public String mainPage(){
        return "/main.html";
    }

    // 获取信息
    @RequestMapping("/userInfo")
    @ResponseBody
    public String getUserSessionInfo(HttpServletRequest request){
        JSONObject jsonObject = new JSONObject();
        String user = (String)request.getSession().getAttribute("user");
        String role = (String)request.getSession().getAttribute("role");
        jsonObject.put("user", user);
        jsonObject.put("role", role);
        return jsonObject.toJSONString();
    }

    @RequestMapping("/logout")
    @ResponseBody
    public String logOut(HttpServletRequest request, HttpServletResponse response){
        request.getSession().setAttribute("user", null);
        // cookie中获取token
        Cookie[] cookies=request.getCookies();
        boolean isOk = false;
        Cookie cookieNow = null;
        if(cookies!=null){
            for(Cookie cookie:cookies) {
                if (cookie.getName().equals("accessToken")) {
                    cookieNow = cookie;
                    System.out.println("获得携带access token的cookie: " + cookie.getValue());
                    isOk = true;
                    break;
                }
            }
        }
        if(isOk) {
            ssoService.logOutFromSSO(request, response, cookieNow.getValue());
        }
        // 没有cookie，直接返回就好了
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "/main");
        return jsonObject.toJSONString();
    }
}
