package com.hackaton.vk_mini_backend.repository;

import com.hackaton.vk_mini_backend.model.RegUserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface RegUserTokenRepo extends JpaRepository<RegUserToken, Long> {

    Optional<RegUserToken> findByToken(String token);

    @Modifying
    int deleteByUserId(Long idUser);
}
