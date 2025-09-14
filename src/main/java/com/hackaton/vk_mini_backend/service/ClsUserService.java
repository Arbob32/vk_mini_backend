package com.hackaton.vk_mini_backend.service;

import com.hackaton.vk_mini_backend.dto.ClsUserDto;
import com.hackaton.vk_mini_backend.exception.NoSuchElementFoundException;
import com.hackaton.vk_mini_backend.exception.UserAlreadyExistsException;
import com.hackaton.vk_mini_backend.model.ClsActivationStatus;
import com.hackaton.vk_mini_backend.model.ClsUser;
import com.hackaton.vk_mini_backend.repository.ClsActivationStatusRepo;
import com.hackaton.vk_mini_backend.repository.ClsUserRepo;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClsUserService {

    private final ClsUserRepo userRepo;
    private final ClsActivationStatusRepo statusRepo;
    private final PasswordEncoder passwordEncoder;
    private static final String USER_NOT_FOUND = "user.not-found";

    public Optional<ClsUser> findByLogin(final String login) {
        return userRepo.findByLoginIgnoreCaseAndIsDeleted(login, false);
    }

    public ClsUser findById(final Long id) {
        return userRepo.findById(id).orElseThrow(() -> new NoSuchElementFoundException(USER_NOT_FOUND));
    }

    @Transactional
    public ClsUserDto register(final ClsUserDto userDto) {
        ClsUser user = createUserEntity(userDto);
        ClsUser savedUser = userRepo.save(user);
        return convertToDto(savedUser);
    }

    @Transactional
    public ClsUser registerAndGetEntity(final ClsUserDto userDto) {
        ClsUser user = createUserEntity(userDto);
        return userRepo.save(user);
    }

    private ClsUser createUserEntity(final ClsUserDto userDto) {
        if (findByLogin(userDto.getLogin()).isPresent()) {
            throw new UserAlreadyExistsException("user.already-exists");
        }

        ClsUser user = new ClsUser();
        user.setLogin(userDto.getLogin());
        user.setName(userDto.getName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setIsDeleted(false);

        ClsActivationStatus status = statusRepo
                .findById(1L)
                .orElseThrow(() -> new NoSuchElementFoundException("activation-status.not-found"));
        user.setStatus(status);

        return user;
    }

    @Transactional
    public ClsUserDto update(final ClsUserDto userDto) {
        ClsUser user =
                userRepo.findById(userDto.getId()).orElseThrow(() -> new NoSuchElementFoundException(USER_NOT_FOUND));

        if (StringUtils.isNotBlank(userDto.getName())) {
            user.setName(userDto.getName());
        }

        if (StringUtils.isNotBlank(userDto.getPassword())) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        ClsUser updatedUser = userRepo.save(user);
        return convertToDto(updatedUser);
    }

    @Transactional
    public void deleteById(final Long id) {
        ClsUser user = userRepo.findById(id).orElseThrow(() -> new NoSuchElementFoundException(USER_NOT_FOUND));
        user.setIsDeleted(true);
        userRepo.save(user);
    }

    public ClsUserDto convertToDto(final ClsUser user) {
        ClsUserDto dto = new ClsUserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setLogin(user.getLogin());
        dto.setIsDeleted(user.getIsDeleted());

        return dto;
    }

    public ClsUserDto activateUser(final Long userId) {
        ClsUser user = userRepo.findById(userId).orElseThrow(() -> new NoSuchElementFoundException(USER_NOT_FOUND));

        ClsActivationStatus activatedStatus = statusRepo
                .findById(2L)
                .orElseThrow(() -> new NoSuchElementFoundException("activation-status.not-found"));
        user.setStatus(activatedStatus);

        ClsUser updatedUser = userRepo.save(user);
        return convertToDto(updatedUser);
    }
}
