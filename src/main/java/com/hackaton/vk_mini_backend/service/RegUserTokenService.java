package com.hackaton.vk_mini_backend.service;

import com.hackaton.vk_mini_backend.exception.TokenRefreshException;
import com.hackaton.vk_mini_backend.model.ClsUser;
import com.hackaton.vk_mini_backend.model.RegUserToken;
import com.hackaton.vk_mini_backend.repository.RegUserTokenRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegUserTokenService {
    @Value("${app.auth.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    private final RegUserTokenRepo regUserTokenRepository;

    public Optional<RegUserToken> findByToken(final String token) {
        return regUserTokenRepository.findByToken(token);
    }

    public RegUserToken createRefreshToken(final ClsUser user) {
        RegUserToken refreshToken = new RegUserToken();

        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = regUserTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RegUserToken verifyExpiration(final RegUserToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            regUserTokenRepository.delete(token);
            throw new TokenRefreshException("Ошибка при обновлении jwt токена");
        }

        return token;
    }

    @Transactional
    public int deleteByUserId(final Long idUser) {
        return regUserTokenRepository.deleteByUserId(idUser);
    }
}
