package org.terminate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目启动类
 * 对应要求：基于 Spring Boot 2.7.x 框架实现
 */
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        // 启动 Spring Boot 应用
        SpringApplication.run(Main.class, args);
        System.out.println("=======================================");
        System.out.println("游戏策略知识共享平台后端 启动成功！");
        System.out.println("=======================================");
    }
}