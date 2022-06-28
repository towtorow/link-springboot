package com.springboot.link.domain.room;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.link.web.dto.ChatMessageDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springboot.link.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Room extends BaseTimeEntity {
    @Id
    private Long id;
    @Column(nullable = false)
    private String pw;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String host;
    @Column(nullable = false)
    private int capacity;
    @Column(nullable = false)
    private int memberCnt;
    @JsonIgnore
    @Transient
    private Set<WebSocketSession> sessions;


    @Builder
    public Room(Long id, String pw, String name, String host, int capacity, Set<WebSocketSession> sessions, int memberCnt) {
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.host = host;
        this.capacity = capacity;
        this.sessions = sessions;
        this.memberCnt = memberCnt;
    }




    public void addSession(WebSocketSession session) {
        if(sessions == null){
            sessions = new HashSet<WebSocketSession>();
        }
        sessions.add(session);
    }

    public void removeSession(WebSocketSession session) {
        if(sessions !=null && sessions.contains(session)) {
            sessions.remove(session);
        }else {
            return;
        }
    }

    public void handleMessage(WebSocketSession session, ChatMessageDto chatMessage) throws Exception {

        if (chatMessage.getType() == ChatMessageDto.JOIN) {
            addSession(session);
            chatMessage.setMessage(chatMessage.getWriter() + "님이 입장하셨습니다.");
        }

        send(chatMessage);
    }

    private void send(ChatMessageDto message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        for (WebSocketSession session : sessions) {
            session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
        }
    }


    @Override
    public String toString() {
        return "Room [id=" + id + ", pw=" + pw + ", name=" + name + ", host=" + host + ", capacity=" + capacity
                + ", sessions=" + sessions + "]";
    }

}