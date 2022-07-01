package com.springboot.link.config;
import com.springboot.link.domain.room.Room;
import com.springboot.link.domain.room.RoomRepository;
import com.springboot.link.service.RoomService;
import com.springboot.link.web.ChatController;
import com.springboot.link.web.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.*;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

@EnableWebSocketMessageBroker
@Configuration
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger log = LoggerFactory.getLogger(StompWebSocketConfig.class);

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomService roomService;
    //endpoint를 /stomp로 하고, allowedOrigins를 "*"로 하면 페이지에서
    //Get /info 404 Error가 발생한다. 그래서 아래와 같이 2개의 계층으로 분리하고
    //origins를 개발 도메인으로 변경하니 잘 동작하였다.
    //이유는 왜 그런지 아직 찾지 못함
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp/chat")
                .setAllowedOrigins("*")
                //.setAllowedOrigins("https://link-springboot-app-link-springboot-app-production.azuremicroservices.io")
                //.setAllowedOrigins("http://localhost:8080")
                .withSockJS();
    }

    /*어플리케이션 내부에서 사용할 path를 지정할 수 있음*/
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/pub");
        registry.enableSimpleBroker("/sub");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.addDecoratorFactory(new WebSocketHandlerDecoratorFactory() {
            @Override
            public WebSocketHandler decorate(final WebSocketHandler handler) {
                return new WebSocketHandlerDecorator(handler) {

                    @Override
                    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

                        log.info(session.getId());
                        Long roomId = roomService.getRoomId(session);



                        Room room = roomRepository.findById(roomId).get();
                        room.setMemberCnt(room.getMemberCnt() - 1);
                        roomService.updateRoom(room);


                    }

                    @Override
                    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                        super.handleMessage(session, message);
                        log.info(session.getId() + "|"+message.getPayload());
                        if(message.getPayload().toString().startsWith("SUBSCRIBE")){
                            String roomIdStr = message.getPayload().toString().split("/sub/chat/room/")[1];
                            String roomIdStrExtracted = roomIdStr.replaceAll("[^0-9]","").trim();
                            Long roomId = Long.parseLong(roomIdStrExtracted);
                            log.info(roomId+"||"+ session.getId());
                            roomService.addSession(roomId, session.getId());
                        }

                    }
                };
            }
        });
    }

}