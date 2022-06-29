package com.springboot.link.service;

import com.springboot.link.domain.room.Room;
import com.springboot.link.domain.room.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.data.repository.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Service
public class RoomService {

    private RoomRepository roomRepository;
    private Map<Long, Room> roomMap;
    private Map<String, Long> sessionIdRoomIdMap;
    @Autowired
    public RoomService(RoomRepository roomRepository){
        this.roomRepository = roomRepository;
        if(roomMap == null){
            roomMap = new HashMap<Long, Room>();
        }
        if(sessionIdRoomIdMap == null){
            sessionIdRoomIdMap = new HashMap<String, Long>();
        }
    }

    public void addRoom(Room room){
        Long id = 1L;
        if(!roomRepository.findMaxId().isEmpty()){
            id = roomRepository.findMaxId().get(0).getId() + 1;
        }
        room.setId(id);
        roomRepository.save(room);
        if(!roomMap.containsKey(room.getId())){
            roomMap.put(room.getId(), room);
        }

    }
    public String deleteRoom(Long id){
        if(roomMap.containsKey(id)){
            roomMap.remove(id);
        }
        if(!roomRepository.findById(id).isEmpty()){
            roomRepository.deleteById(id);
        }
        return "Success";
    }
    public void updateRoom(Room room){
        if(roomMap.containsKey(room.getId())){
            roomMap.get(room.getId()).setCapacity(room.getCapacity());
            roomMap.get(room.getId()).setHost(room.getHost());
            roomMap.get(room.getId()).setMemberCnt(room.getMemberCnt());
            roomMap.get(room.getId()).setPw(room.getPw());
            roomMap.get(room.getId()).setName(room.getName());
        }
        if(!roomRepository.findById(room.getId()).isEmpty()){
            roomRepository.save(room);
        }
    }
    public void removeSession(WebSocketSession session) {
        for (Room room : roomMap.values()) {
            room.removeSession(session);
        }
    }
    public void addSession(Long id, String sessionId) {

        sessionIdRoomIdMap.put(sessionId, id);
    }
    public Long getRoomId(WebSocketSession session) {
        return sessionIdRoomIdMap.get(session.getId());
    }
    public Room getRoomsForSessions(Long id) {
        return roomMap.get(id);
    }

}
