package org.reggie.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.reggie.common.BaseContext;
import org.reggie.common.R;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
过滤器：检查用户是否已经登录
过滤器逻辑见下

 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    // 路径匹配器（工具类，支持通配符：
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // ServletRequest向下转型HttpServletRequest
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 过滤器逻辑：
        // - 获取本次请求URI，把不需要处理的路径/login和/logout放在String数组里
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}", request.getRequestURI());

        // 定义不需要处理的请求路径
        String[] urls = new String[] {
          "/employee/login",
          "/employee/logout",
          "/backend/**", //静态资源
          "/front/**", //静态资源
          "/common/**",
          "/user/sendMsg", //移动端发送短信
          "/user/login" //移动端登录
        };
        // - check方法 - 判断本次请求是否需要处理，否则放行
        boolean check = check(requestURI, urls);

        if(check) {
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        // - 判断登录状态getSession，已登录则放行
        if (request.getSession().getAttribute("employee") != null) {
            log.info("用户已登录，id{}",request.getSession().getAttribute("employee"));

            // BaseContext传入id
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("employee"));

            // 放行
            filterChain.doFilter(request,response);
            return;
        }

        // - 判断 移动端 登录状态getSession，已登录则放行
        if (request.getSession().getAttribute("user") != null) {
            log.info("用户已登录，id{}",request.getSession().getAttribute("user"));

            // BaseContext传入id
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("user"));

            // 放行
            filterChain.doFilter(request,response);
            return;
        }

        // - 未登录则返回未登录结果,通过 输出流 的方式回写：data.code, data.msg
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 路径匹配，检查请求是否处理
     * @param requestURI
     * @param urls
     * @return
     */
    public boolean check(String requestURI, String[] urls) {
        // 遍历String数组urls，逐一和得到的URI比对
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match) return true;
        }
        return false;
    }

}
