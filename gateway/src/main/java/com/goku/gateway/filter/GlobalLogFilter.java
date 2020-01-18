package com.goku.gateway.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fuyongde
 * @date 2020/1/18 16:19
 */
@Component
@Slf4j
public class GlobalLogFilter implements GatewayFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Map<String, Object> logMap = new HashMap<>(16);
        Object requestBody = exchange.getAttribute("cachedRequestBodyObject");
        long start = System.currentTimeMillis();

        logMap.put("startTime", start);
        logMap.put("requestBody", JSON.toJSONString(requestBody));

        ServerHttpResponse response = exchange.getResponse();
        DataBufferFactory bufferFactory = response.bufferFactory();

        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(response) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(fluxBody.map(dataBuffer -> {
                        // probably should reuse buffers
                        byte[] content = new byte[dataBuffer.readableByteCount()];
                        String response = new String(content, StandardCharsets.UTF_8);
                        long end = System.currentTimeMillis();
                        logMap.put("endTime", end);
                        logMap.put("responseBody : {}", JSON.toJSONString(response));
                        // 这里一定要 release DataBuffer，不然会有堆外内存溢出的风险
                        DataBufferUtils.release(dataBuffer);
                        return bufferFactory.wrap(content);
                    }));
                } else {
                    return super.writeWith(body);
                }
            }
        };

        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    @Override
    public int getOrder() {
        return -5;
    }
}
