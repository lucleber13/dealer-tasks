package com.cbcode.dealertasks.Users.service;

import com.cbcode.dealertasks.Users.model.DTOs.UserDto;
import com.cbcode.dealertasks.Users.service.impl.DTOsResponses.UserDeletionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface AdminUserService {

    UserDeletionResponse deleteUser(Long id);

    Page<UserDto> getAllUsers(Pageable pageable);

    UserDto getUserById(Long id);

    UserDto adminUpdateUser(Long id, UserDto userDto);

    UserDto disableUser(Long id);

    UserDto enableUser(Long id);

    // Update the Role of a User by ID and Role ID
    UserDto updateUserRole(Long id, Set<String> roleNames);
}
