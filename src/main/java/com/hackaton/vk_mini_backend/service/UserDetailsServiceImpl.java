package com.hackaton.vk_mini_backend.service;

import com.hackaton.vk_mini_backend.model.ClsUser;
import com.hackaton.vk_mini_backend.repository.ClsUserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final ClsUserRepo userRepo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) {
        ClsUser user = userRepo.findByLoginIgnoreCaseAndIsDeleted(username, false)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Пользователь не найден с именем пользователя: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getLogin(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
