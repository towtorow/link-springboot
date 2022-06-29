package com.springboot.link.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageDto {
    public static final int JOIN = 1;

    private Long roomId;

    private String writer;

    private String message;
    private int type;


    @Builder
    public ChatMessageDto(Long roomId, String writer, String message, int type) {

        this.roomId = roomId;
        this.writer = writer;
        this.message = message;
        this.type= type;
    }


    @Override
    public String toString() {
        return "ChatMessageDto [roomId=" + roomId + ", writer=" + writer + ", message=" + message + ", type=" + type + "]";
    }

}