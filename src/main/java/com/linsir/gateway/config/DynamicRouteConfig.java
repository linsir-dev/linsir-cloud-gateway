package com.linsir.gateway.config;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.linsir.gateway.route.NacosRouteDefinitionRepository;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author yuxiaolin
 * @title: DynamicRouteConfig
 * @projectName linsir
 * @description: TODO
 * @date 2022/2/21 9:44 下午
 */
@Configuration
public class DynamicRouteConfig {
    @Resource
    private ApplicationEventPublisher publisher;

    @Resource
    private NacosConfigProperties nacosConfigProperties;

    @Bean
    public NacosRouteDefinitionRepository nacosRouteDefinitionRepository() {
        return new NacosRouteDefinitionRepository(publisher, nacosConfigProperties);
    }
}
