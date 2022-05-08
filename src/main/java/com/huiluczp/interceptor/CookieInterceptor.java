package com.huiluczp.interceptor;

import com.huiluczp.service.SSOService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Component
public class CookieInterceptor implements HandlerInterceptor {

    @Autowired
    SSOService ssoService;

    // 如果有session，那么直接放行
    // 如果有cookie，就转发给sso进行验证，验证后通过后给予session
    // 没有cookie或验证不通过，跳转login页面
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session=request.getSession();
        Object role = session.getAttribute("role");
        Object user = session.getAttribute("user");
        if(role!=null&&user!=null){
            return true;
        }else{
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
            if(isOk){
                // 进行验证
                ssoService.validateTokenFromSSO(request, response, cookieNow.getValue());
            }else{
                // 跳转到统一登陆页面
                System.out.println("不存在cookie信息");
                ssoService.redirectToLoginPage(request, response);
            }
        }
        return true;
    }
}
