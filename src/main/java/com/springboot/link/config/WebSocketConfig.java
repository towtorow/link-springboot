package com.springboot.link.config;
import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.websocket.api.WebSocketBehavior;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.jetty.JettyRequestUpgradeStrategy;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import javax.servlet.ServletContext;
import java.security.Principal;
import java.util.UUID;

@RequiredArgsConstructor
@EnableWebSocket
@Configuration
public class WebSocketConfig implements WebSocketConfigurer  {


    private final EchoHandler echoHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(echoHandler, "/link/echo/info").setHandshakeHandler(new DefaultHandshakeHandler((new JettyRequestUpgradeStrategy())));
    }
    @Bean
    public ThreadPoolTaskScheduler taskScheduler(TaskSchedulerBuilder builder) {
        return builder.build();
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        return container;
    }

}