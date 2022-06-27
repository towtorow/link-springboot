package com.springboot.link.service;

import com.springboot.link.domain.room.Room;
import com.springboot.link.domain.room.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Service
public class RoomService {

    private RoomRepository roomRepository;
    private Map<Long, Room> roomMap;

    @Autowired
    public RoomService(RoomRepository roomRepository){
        this.roomRepository = roomRepository;
        if(roomMap == null){
            roomMap = new HashMap<Long, Room>();
        }
    }

    public void addRoom(Room room){
        if(!roomMap.containsKey(room.getId())){
            roomMap.put(room.getId(), room);
        }
        if(roomRepository.findById(room.getId()).isEmpty()){
            roomRepository.save(room);
        }
    }
    public void deleteRoom(Room room){
        if(roomMap.containsKey(room.getId())){
            roomMap.remove(room.getId());
        }
        if(!roomRepository.findById(room.getId()).isEmpty()){
            roomRepository.deleteById(room.getId());
        }
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
    public void addSession(Long id, WebSocketSession session) {
        roomMap.get(id).addSession(session);
    }
    public Room getRoomsForSessions(Long id) {
        return roomMap.get(id);
    }

}
