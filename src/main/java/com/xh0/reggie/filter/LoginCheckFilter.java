package com.xh0.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import com.xh0.reggie.common.BaseContext;
import com.xh0.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            HttpServletRequest request = (HttpServletRequest)servletRequest;
            HttpServletResponse response = (HttpServletResponse)servletResponse;

//      1.获取本次请求uri
        String requestURI = request.getRequestURI();
        log.info("拦截到请求:{}",requestURI);
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
//        2.判断是否需要处理
        boolean check = check(urls,requestURI);


//        3.不需要处理就放行
        if (check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        };
//        4-1.已登录放行
        if(request.getSession().getAttribute("employee")!=null){
            log.info("用户已登录，用户id为{}",request.getSession().getAttribute("employee"));
            long empId = (Long)request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        };
        //        4-2.手机端已登录放行
        if(request.getSession().getAttribute("user")!=null){
            log.info("用户已登录，用户id为{}",request.getSession().getAttribute("user"));
            long userId = (Long)request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        };
//        5.未登录，输出流方式
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return ;

    }
    public boolean check(String[] urls,String requestURI){
        for(String url : urls){
            boolean match = ANT_PATH_MATCHER.match(url,requestURI);
            if(match){
                return true;
            }
        };
        return false;
    }
}




