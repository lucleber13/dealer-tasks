package com.cbcode.dealertasks.Users.service.impl.DTOsResponses;

import java.time.LocalDateTime;

public record UserDeletionResponse(
        Long id,
        String email,
        String message,
        LocalDateTime deletedAt
) {
}
