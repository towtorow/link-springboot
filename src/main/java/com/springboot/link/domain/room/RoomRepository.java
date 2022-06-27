package com.springboot.link.domain.room;

import com.springboot.link.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface RoomRepository extends JpaRepository<Room, Long>  {


}
