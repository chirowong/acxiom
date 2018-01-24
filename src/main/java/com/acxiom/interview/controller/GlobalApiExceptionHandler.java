package com.acxiom.interview.controller;

import com.acxiom.interview.exception.SearchException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

/**
 * web应用层全局异常处理者
 * controller中的异常最终都上抛到此处理器中
 * 此处理器应当按照客户端请求响应规范返回http status,等异常信息
 * Created by janus on 15/7/8.
 */
@ControllerAdvice(annotations = RestController.class)
public class GlobalApiExceptionHandler extends BaseController {

    public GlobalApiExceptionHandler() {
        logger.info("异常处理器初始化成功");
    }

    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ExceptionContent> allExceptionHandler(Throwable throwable) {
        // 进行异常分类和日志处理
        HttpStatus httpStatus = HttpStatus.OK;
        String errorCode = "500000";
        String errorMsg = "未知错误";
        String errorDetail = null;

        Throwable e = throwable;

        // 如果是有定义的异常
        if (e instanceof SearchException) {
            SearchException exception = (SearchException) e;
//            if (exception.getHttpStatus() != null) {
//                httpStatus = exception.getHttpStatus();
//            }
            if (StringUtils.isNotBlank(exception.getErrorCode())) {
                errorCode = exception.getErrorCode();
            }
            if (StringUtils.isNotBlank(exception.getMessage())) {
                errorMsg = exception.getMessage();
            }
        } else if (e instanceof IllegalArgumentException) {
            // 非法参数的异常, 通常来自于Assert断言判断, 如果是正常业务会出现的断言出错应当由断言发起方给出正确的错误消息
            errorMsg = e.getMessage();
        } else {
            errorDetail = e.getMessage();
        }

        try {
            String warningMsg = String.format("搜索服务异常: errorCode=%s, errorMsg=%s, errorDetail=%s", errorCode, errorMsg, errorDetail);
            logger.error(warningMsg);
        } catch (Exception e1) {
            logger.error(e1.getMessage(), e1);
        }

        return new ResponseEntity<>(new ExceptionContent(errorCode, errorMsg), httpStatus);
    }

    /**
     * 自定义http 对象模型
     */
    private class ExceptionContent implements Serializable {

        private int code;
        private String message;

        /**
         * 业务异常构造器
         *
         * @param errorCode 错误码
         * @param errorMsg 错误信息
         */
        ExceptionContent(String errorCode, String errorMsg) {
            code = Integer.valueOf(errorCode);
            message = errorMsg;

        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

}
