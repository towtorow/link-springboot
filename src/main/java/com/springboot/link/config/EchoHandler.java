package com.springboot.link.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.link.domain.room.Room;
import com.springboot.link.domain.room.RoomRepository;
import com.springboot.link.service.RoomService;
import com.springboot.link.web.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@RequiredArgsConstructor
@Service
public class EchoHandler extends TextWebSocketHandler {

    private final RoomService roomService;
    private final RoomRepository roomRepository;
    private static Logger log = LoggerFactory.getLogger(EchoHandler.class);
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri().getQuery();
        log.info(query);

        Long roomId = Long.parseLong(query);
        Room room = roomRepository.findById(roomId).get();

        if (room.getMemberCnt() + 1 < room.getCapacity()) {
            room.setMemberCnt(room.getMemberCnt() + 1);
            roomService.updateRoom(room);
            roomService.addSession(roomId, session);
        }else{
            session.close();
        }

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ChatMessageDto chatMessage = mapper.readValue(message.getPayload().toString(), ChatMessageDto.class);
        Room room = roomService.getRoomsForSessions(chatMessage.getRoomId());
        room.handleMessage(session, chatMessage);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String query = session.getUri().getQuery();
        String roomId = query;

        Room room = roomRepository.findById(Long.parseLong(roomId)).get();
        room.setMemberCnt(room.getMemberCnt() - 1);
        roomService.updateRoom(room);
        roomService.removeSession(session);

    }

}