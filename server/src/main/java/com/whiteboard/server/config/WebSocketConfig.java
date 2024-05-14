package com.whiteboard.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册一个WebSocket端点，客户端将使用它连接到WebSocket服务。
        logger.info("Registering STOMP endpoint at /ws");
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        logger.info("Configuring message broker with application destination prefix /app and simple broker /board");

        // 配置一个简单的消息代理，消息可以路由到指定的URL前缀。
        config.enableSimpleBroker("/board");
        // 定义应用程序前缀，客户端发送数据的路径应以/app开始。
        config.setApplicationDestinationPrefixes("/app");
    }
}
