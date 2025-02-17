package com.cbcode.dealertasks.Users.service;

import com.cbcode.dealertasks.Users.model.DTOs.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDto updateUser(Long id, UserDto userDto);

    String deleteUser(Long id);

    Page<UserDto> getAllUsers(Pageable pageable);

    UserDto getUserById(Long id);

    UserDto adminUpdateUser(Long id, UserDto userDto);

    void disableUser(Long id);

    void enableUser(Long id);
}
