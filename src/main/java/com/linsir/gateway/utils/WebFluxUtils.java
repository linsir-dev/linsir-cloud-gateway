package com.linsir.gateway.utils;

import cn.hutool.json.JSONUtil;
import com.linsir.base.core.code.BaseCode;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author ：linsir
 * @date ：Created in 2022/9/8 1:06
 * @description：异常响应工具类
 * @modified By：
 * @version: 0.0.1
 */
public class WebFluxUtils {

    public static Mono<Void> writeResponse(ServerHttpResponse response, BaseCode resultCode) {
        switch (resultCode) {
            case UNAUTHORIZED:
            case FAIL_INVALID_TOKEN:
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                break;
            case TOKEN_ACCESS_FORBIDDEN:
                response.setStatusCode(HttpStatus.FORBIDDEN);
                break;
            default:
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                break;
        }
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getHeaders().set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.getHeaders().set(HttpHeaders.CACHE_CONTROL, "no-cache");
        String body = JSONUtil.toJsonStr("XXXXXXXX");
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer))
                .doOnError(error -> DataBufferUtils.release(buffer));
    }
}
