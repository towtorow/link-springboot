package com.springboot.link.web;

import com.springboot.link.config.auth.LoginUser;
import com.springboot.link.config.auth.dto.SessionUser;
import com.springboot.link.domain.room.Room;
import com.springboot.link.domain.room.RoomRepository;
import com.springboot.link.service.RoomService;
import com.springboot.link.web.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class ChatController {


    private final RoomRepository roomRepository;

    private final RoomService roomService;

    private final SimpMessagingTemplate template;
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);


    @GetMapping("/link/echo")
    public String chat(){
        return "link/echo";
    }

    @GetMapping("/form/room/create")
    public String roomCreateForm(Model model, @LoginUser SessionUser user) {
        if (user != null) {
            model.addAttribute("userEmail", user.getEmail());
        }
        return "roomCreateForm";
    }
    @PostMapping("/api/room/selectAll")
    @ResponseBody
    public ArrayList<Room> rooms() {
        try {
            return new ArrayList<Room>(roomRepository.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/api/room/select/{id}")
    @ResponseBody
    public Room room(@PathVariable String id) {
        log.info(id);
        Long parsedId = Long.parseLong(id);
        return roomRepository.findById(parsedId).get();

    }

    @GetMapping("/room/enter")
    public String enterRoom(@RequestParam String id, @LoginUser SessionUser sessionUser, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("userEmail", sessionUser.getEmail());
        Long roomId = Long.parseLong(id);
        Room room = roomRepository.findById(roomId).get();

        if (room.getMemberCnt() + 1 <= room.getCapacity()) {
            room.setMemberCnt(room.getMemberCnt() + 1);
            roomService.updateRoom(room);
            return "room";
        }else{
            return "index";
        }

    }

    @MessageMapping(value = "/chat/enter")
    public void enter(ChatMessageDto message){
        message.setMessage(message.getWriter() + "님이 채팅방에 참여하였습니다.");
        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }

    @MessageMapping(value = "/chat/message")
    public void message(ChatMessageDto message){

        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }
    @GetMapping("/room/exit")
    public String exitRoom(@RequestParam String id, Model model, @LoginUser SessionUser user) {
        ChatMessageDto message = ChatMessageDto.builder().message(user.getEmail() + "님이 채팅방에서 퇴장하였습니다.").writer("System").build();
        template.convertAndSend("/sub/chat/room/" + id, message);
        Long roomId = Long.parseLong(id);
        Room room = roomRepository.findById(roomId).get();
        room.setMemberCnt(room.getMemberCnt() - 1);
        roomService.updateRoom(room);
        return "index";
    }

    @PostMapping("/api/room/delete")
    @ResponseBody
    public String delete(@RequestBody  String roomId) {

        try {
            Long longId = Long.parseLong(roomId);
            Room room = roomRepository.findById(longId).get();
            if(room.getMemberCnt() != 1){
                return "existMemeber";
            }
            roomService.deleteRoom(longId);
            return "Success";
        } catch (Exception e) {

            e.printStackTrace();
            return e.getMessage();
        }

    }


    @PostMapping("/api/room/create")
    @ResponseBody
    public String create(@RequestBody Room room) {

        try {

            roomService.addRoom(room);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }

    @ResponseBody
    @RequestMapping("/api/room/update")
    public String update(@RequestBody Room room, @LoginUser SessionUser user) throws Exception {
        try{
            Room roomById = roomRepository.findById(room.getId()).get();
            if(!(roomById != null && roomById.getHost()!=null && roomById.getHost().equals(user.getEmail()))){
                log.info(roomById.getHost()+"|"+user.getEmail());
                return "HostMisMatch";
            }
            roomService.updateRoom(room);
            return "Success";

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }


    }

    @GetMapping("/form/room/modify")
    public String roomModifyForm(@RequestParam String id, Model model, @LoginUser SessionUser user) {
        Room room = roomRepository.findById(Long.parseLong(id)).get();

        model.addAttribute("user", user.getEmail());
        model.addAttribute("id", id);
        model.addAttribute("pw", room.getPw());
        model.addAttribute("name", room.getName());
        model.addAttribute("capacity", room.getCapacity());
        return "roomModifyForm";
    }

}