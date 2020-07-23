package com.lxh.seckill.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * created by lanxinghua@2dfire.com on 2020/7/21
 */
@Configuration
public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointConfig(){
        return new ServerEndpointExporter();
    }
}
