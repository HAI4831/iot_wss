package com.run.iot_websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class MoveWebSocketHandler implements WebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper(); // Hoặc inject từ Spring
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    private ActionMoveService actionMoveService;

    @Autowired
    public void setActionService(ActionMoveService actionService) {
        this.actionMoveService = actionService;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        sessions.add(session);
        return session.receive()
                .map(webSocketMessage -> webSocketMessage.getPayloadAsText())
                .flatMap(message -> {
                    try {
                        // Chuyển đổi JSON thành ActionMoveRequest
                        ActionMoveRequest moveRequest = objectMapper.readValue(message, ActionMoveRequest.class);

                        // Xử lý yêu cầu di chuyển
                        ActionMoveResponse moveResponse = processMove(moveRequest);

                        // Chuyển đổi phản hồi thành JSON
                        String responseJson = objectMapper.writeValueAsString(moveResponse);

                        // Gửi phản hồi trở lại client
                        return session.send(Mono.just(session.textMessage(responseJson)));
                    } catch (Exception e) {
                        // Xử lý lỗi
                        ActionMoveResponse errorResponse = new ActionMoveResponse("ERROR", e.getMessage());
                        try {
                            String errorJson = objectMapper.writeValueAsString(errorResponse);
                            return session.send(Mono.just(session.textMessage(errorJson)));
                        } catch (Exception ex) {
                            return Mono.empty();
                        }
                    }
                })
                .doFinally(signalType -> sessions.remove(session))  // Loại bỏ session khi kết thúc
                .then();
    }

    private ActionMoveResponse processMove(ActionMoveRequest moveRequest) {
        // Lấy thông tin lệnh di chuyển từ yêu cầu
        String actionMoveNameStr = moveRequest.getActionMoveName();
        Long speed = moveRequest.getSpeed();

        // Chuyển đổi tên hành động sang enum tương ứng
        ActionMove.ActionMoveName actionMoveName = ActionMove.ActionMoveName.valueOf(actionMoveNameStr.toUpperCase());
        // Tạo phản hồi MoveResponse
        String moveMessage = String.format("Command '%s' executed with speed %d", actionMoveName.getEspEndpoint(), speed);
        broadcastCommand("\"action_move_name\":\""+ actionMoveName.getEspEndpoint()+"\",\"speed\":" + speed);

        // Trả về phản hồi
        return new ActionMoveResponse("SUCCESS", moveMessage);
    }

    // Hàm để gửi lệnh tới tất cả các kết nối WebSocket hiện tại
    public void broadcastCommand(String command) {
        try {
            synchronized (sessions) {
                for (WebSocketSession session : sessions) {
                    session.send(Mono.just(session.textMessage(command))).subscribe();
                }
            }
        } catch (Exception e) {
            // Xử lý lỗi nếu cần
            e.printStackTrace();
        }
    }
}
