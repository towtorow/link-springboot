package com.springboot.link.web;

import com.springboot.link.domain.room.Room;
import com.springboot.link.domain.room.RoomRepository;
import com.springboot.link.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RequiredArgsConstructor
@RestController("/chat")
public class ChatController {


    private final RoomRepository roomRepository;

    private final RoomService roomService;





    @GetMapping(value = { "/roomsGrid" })
    public String roomsGrid() {
        return "roomsGrid";

    }

    @GetMapping(value = { "/roomCreateForm" })
    public String roomCreateForm() {
        return "roomCreateForm";
    }

    @GetMapping("/rooms")
    public ArrayList<Room> rooms() {
        try {
            return new ArrayList<Room>(roomRepository.findAll());
        } catch (Exception e) {
            e.printStackTrace();

        }finally {
            return null;
        }
    }

    @GetMapping("/rooms/{id}")
    public Room room(@PathVariable Long id) {

        return roomRepository.findById(id).get();

    }

    @PostMapping("/room")
    public String enterRoom(@RequestParam String id, Model model) {
        model.addAttribute("id", id);
        return "room";
    }


    @PostMapping("/room/delete")
    public void delete(@RequestBody Room room) {
        Long id = room.getId();

        try {
            roomService.deleteRoom(room);
        } catch (Exception e) {

            e.printStackTrace();
        }finally{
            return;
        }

    }

    @ResponseBody
    @RequestMapping("/room/create")
    public void create(@RequestBody Room room) {

        try {
            roomService.addRoom(room);
        } catch (Exception e) {
            e.printStackTrace();

        }finally {
            return;
        }

    }

    @ResponseBody
    @RequestMapping("/room/update")
    public void update(@RequestBody Room room) throws Exception {
        try{
            roomService.updateRoom(room);

        } catch (Exception e) {
            e.printStackTrace();

        }finally {
            return;
        }


    }

    @RequestMapping("/roomModifyForm")
    public String roomModifyForm(@RequestParam String id, Model model) {
        model.addAttribute("id", id);
        return "roomModifyForm";
    }

}