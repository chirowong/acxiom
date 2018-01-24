package com.acxiom.interview;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author wangzhiliang
 */
@SpringBootApplication
public class WebApplication {

    private static Logger logger = LoggerFactory.getLogger(WebApplication.class);

    public static void main(String[] args) {
        try {
            logger.info("准备启动acxiom-interview应用");
            SpringApplication.run(WebApplication.class, args);
            logger.info("acxiom-interview应用启动成功");
        } catch (Exception e) {
            logger.error("acxiom-interview应用启动失败", e);
        }
    }
}
