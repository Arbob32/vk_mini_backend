package com.hackaton.vk_mini_backend.repository;

import com.hackaton.vk_mini_backend.model.ClsUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClsUserRepo extends JpaRepository<ClsUser, Long> {
    Optional<ClsUser> findByLoginIgnoreCaseAndIsDeleted(String login, Boolean isDeleted);
}
