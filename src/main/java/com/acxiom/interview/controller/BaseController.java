package com.acxiom.interview.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 为所有Controller的基类
 * @author wangzhiliang
 */
public abstract class BaseController {
    /**
     * 日志类
     */
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 空串
     */
    protected static String EMPTY_STR = StringUtils.EMPTY;

    /**
     * 获得当前请求对象
     *
     * @return
     */
    protected HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 添加HttpResponse header
     *
     * @param name
     * @param value
     */
    protected void addHeader(String name, String value) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        response.addHeader(name, value);
    }

    /**
     * 设置HttpResponse header
     *
     * @param name
     * @param value
     */
    protected void setHeader(String name, String value) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        response.setHeader(name, value);
    }

    protected String getHeader(String name){
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader(name);
    }

    protected static String[] httpProxyHeaderName = new String[]{
            "CDN-SRC-IP",
            "HTTP_CDN_SRC_IP",
            "CLIENTIP",
            "X-FORWARDED-FOR",
    };

    protected String getClientIP() {
        HttpServletRequest request = getRequest();
        for (String headerName : httpProxyHeaderName) {
            String clientIP = request.getHeader(headerName);
            if (StringUtils.isNotBlank(clientIP)) {
                return clientIP;
            }
        }
        return request.getRemoteAddr();
    }
}
