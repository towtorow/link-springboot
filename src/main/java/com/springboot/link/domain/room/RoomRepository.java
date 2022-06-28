package com.springboot.link.domain.room;

import com.springboot.link.domain.posts.Posts;
import com.springboot.link.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
public interface RoomRepository extends JpaRepository<Room, Long>  {

    @Query("SELECT r FROM Room r ORDER BY r.id DESC")
    List<Room> findMaxId();
}
