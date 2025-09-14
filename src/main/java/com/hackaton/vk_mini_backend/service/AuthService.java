package com.hackaton.vk_mini_backend.service;

import com.hackaton.vk_mini_backend.config.security.jwt.JwtUtils;
import com.hackaton.vk_mini_backend.dto.ClsUserDto;
import com.hackaton.vk_mini_backend.dto.request.LoginRequest;
import com.hackaton.vk_mini_backend.dto.request.RegisterRequest;
import com.hackaton.vk_mini_backend.dto.response.JwtResponse;
import com.hackaton.vk_mini_backend.dto.response.MessageResponse;
import com.hackaton.vk_mini_backend.dto.response.TokenRefreshResponse;
import com.hackaton.vk_mini_backend.exception.TokenRefreshException;
import com.hackaton.vk_mini_backend.model.ClsUser;
import com.hackaton.vk_mini_backend.model.RegUserToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final ClsUserService userService;

    private final JwtUtils jwtUtils;

    private final RegUserTokenService refreshTokenService;

    public JwtResponse login(final LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails.getUsername());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        ClsUser user = userService
                .findByLogin(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        refreshTokenService.deleteByUserId(user.getId());

        RegUserToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new JwtResponse(jwt, refreshToken.getToken(), user.getId(), user.getLogin(), user.getName());
    }

    public MessageResponse register(final RegisterRequest registerRequest) {
        MessageResponse response;
        if (userService.findByLogin(registerRequest.getLogin()).isPresent()) {
            response = new MessageResponse("Ошибка: Пользователь с таким логином уже существует!");
        } else {
            ClsUserDto userDto = new ClsUserDto();
            userDto.setLogin(registerRequest.getLogin());
            userDto.setPassword(registerRequest.getPassword());
            userDto.setName(registerRequest.getName());
            userService.register(userDto);
            response = new MessageResponse("Пользователь успешно зарегистрирован!");
        }
        return response;
    }

    public TokenRefreshResponse refreshToken(final String refreshToken) {
        return refreshTokenService
                .findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RegUserToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateJwtToken(user.getLogin());
                    return new TokenRefreshResponse(token, refreshToken);
                })
                .orElseThrow(() -> new TokenRefreshException("Ошибка обновления токена"));
    }

    public MessageResponse logout(final String username) {
        ClsUser user =
                userService.findByLogin(username).orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        refreshTokenService.deleteByUserId(user.getId());
        return new MessageResponse("Выход выполнен успешно!");
    }
}
