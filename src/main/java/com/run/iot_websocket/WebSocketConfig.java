package com.run.iot_websocket;

//import com.run.iot_websocket_3.controllers.MoveWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.HashMap;
import java.util.Map;
@Configuration
public class WebSocketConfig  {
    @Bean
    public HandlerMapping webSocketHandlerMapping(MoveWebSocketHandler moveWebSocketHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/ws", moveWebSocketHandler); // Thay đổi đường dẫn thành "/ws"

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(10); // Đảm bảo thứ tự ưu tiên
        mapping.setUrlMap(map);
        return mapping;
    }
    @Bean
    public MoveWebSocketHandler moveWebSocketHandler() {
        return new MoveWebSocketHandler();
    }
}
