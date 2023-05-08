package com.naevis.bot.repository;

import java.util.List;
import java.util.Optional;

import com.naevis.bot.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByTelegramId(Long telegramId);
    @Query("SELECT u FROM AppUser u JOIN u.rooms r WHERE r.code = :roomCode")
    List<AppUser> findByRoomCode(@Param("roomCode") String roomCode);
}
