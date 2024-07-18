package com.linsir.gateway;

import com.linsir.core.launch.LinsirApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author linsir
 * @title: GatewayApplication
 * @projectName lins
 * @description: 网关入口
 * @date 2021/12/26 18:57
 */
@SpringBootApplication(scanBasePackages="com.linsir")
@EnableDiscoveryClient
@EnableFeignClients
public class GatewayApplication implements CommandLineRunner {


    public static void main(String[] args) {
        LinsirApplication.run("linsir-gateway", GatewayApplication.class,args);
    }


    @Override
    public void run(String... args) throws Exception {
        System.out.println("GatewayApplication,初始化...");
    }
}
