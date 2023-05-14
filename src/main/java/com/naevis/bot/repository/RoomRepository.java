package com.naevis.bot.repository;

import java.util.List;
import java.util.Optional;

import com.naevis.bot.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT r FROM Room r JOIN FETCH r.joinedUsers WHERE r.code = :code")
    Optional<Room> findByCodeWithUsers(@Param("code") String code);

    @Query("SELECT r FROM Room r JOIN FETCH r.joinedUsers u WHERE u.id = :userId")
    List<Room> findRoomsByUserId(@Param("userId") Long userId);
}
