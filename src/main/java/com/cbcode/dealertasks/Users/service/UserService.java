package com.cbcode.dealertasks.Users.service;

import com.cbcode.dealertasks.Users.model.DTOs.UserDto;
import com.cbcode.dealertasks.Users.model.Enums.EnumRole;
import com.cbcode.dealertasks.Users.model.Role;
import com.cbcode.dealertasks.Users.service.impl.DTOsResponses.UserDeletionResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface UserService {

    UserDto updateUser(Long id, UserDto userDto);
}
