package com.linsir.gateway.security;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.IoUtil;
import com.linsir.gateway.utils.WebFluxUtils;
import com.linsir.base.core.code.BaseCode;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author yuxiaolin
 * @title: ResourceServerConfig
 * @projectName linsir
 * @description: 资源服务器
 * @date 2022/1/23 12:33 下午
 */

@AllArgsConstructor
@Configuration
@EnableWebFluxSecurity
public class ResourceServerConfig {

    @Resource
    private AuthorizationManager authorizationManager;

    @Resource
    private CustomServerAccessDeniedHandler customServerAccessDeniedHandler;

    @Resource
    private CustomServerAuthenticationEntryPoint customServerAuthenticationEntryPoint;
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
       http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter())
               .publicKey(rsaPublicKey());

               //1.处理白名单
                http.oauth2ResourceServer().authenticationEntryPoint(customServerAuthenticationEntryPoint);
                 http.authorizeExchange()
                //.pathMatchers("/linsir-saas-server/oauth/token").permitAll()
                // saas所有资环全部放过
                .pathMatchers("/linsir-saas-server/**","/linsir-cms-server/v1/f/**","/linsir-auth-server/**","/linsir-auth-server/**").permitAll()
                         .pathMatchers("/linsir-saas-server/v2/login",
                                 "/linsir-saas-server/jwt/login",
                                 "/linsir-saas-server/jwt/logout",
                                 "/linsir-saas-server/v2/sysTenant/getByDomain",
                                 "/login",
                                 "/favicon.ico",
                                 "/cms").permitAll()   //加入测试登录
                .anyExchange().access(authorizationManager)
                .and()
                .exceptionHandling()
                .accessDeniedHandler(customServerAccessDeniedHandler) // 处理未授权
                .authenticationEntryPoint(customServerAuthenticationEntryPoint) //处理未认证
                .and().csrf().disable();

        return http.build();
    }

    @Bean
    ServerAccessDeniedHandler accessDeniedHandler() {
        return (exchange, denied) -> {
            Mono<Void> mono = Mono.defer(() -> Mono.just(exchange.getResponse()))
                    .flatMap(response -> WebFluxUtils.writeResponse(response, BaseCode.UNAUTHORIZED));
            return mono;
        };
    }


    /**
     * @link https://blog.csdn.net/qq_24230139/article/details/105091273
     * ServerHttpSecurity没有将jwt中authorities的负载部分当做Authentication
     * 需要把jwt的Claim中的authorities加入
     * 方案：重新定义ReactiveAuthenticationManager权限管理器，默认转换器JwtGrantedAuthoritiesConverter
     */

    @Bean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    /**
     * 公钥
     * @return
     */

    @SneakyThrows
    @Bean
    public RSAPublicKey rsaPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        ClassPathResource resource = new ClassPathResource("public.key");
        InputStream inputStream = resource.getInputStream();
        String publicKeyData = IoUtil.read(inputStream).toString();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec((Base64.decode(publicKeyData)));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKey rsaPublicKey = (RSAPublicKey)keyFactory.generatePublic(keySpec);
        return rsaPublicKey;
    }

}
