# sso_client
SSO client achieved by springboot.

Springboot实现的基于登陆中心的SSO单点登录系统的client演示。</br>
登录中心部分：[https://github.com/huiluczP/sso_register]( https://github.com/huiluczP/sso_register)</br>
项目简介：[基于登录中心的跨域SSO实现](https://blog.csdn.net/qq_41733192/article/details/124652716)

Client：
1.	拦截器实现身份验证
2.	访问SSO中心验证接口，登录页面
3.	从SSO中心获取非敏感身份信息，session中存放
4.	登出功能，访问SSO中心登出接口

演示：</br>
![login](https://user-images.githubusercontent.com/36394708/167294752-e92bf69c-a913-4171-82d0-cf8bbb79a24f.gif)
![logout](https://user-images.githubusercontent.com/36394708/167294744-3f606fbe-587b-4754-a15f-90f38122ef8a.gif)
