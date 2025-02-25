package com.cbcode.dealertasks.Users.controller;

import com.cbcode.dealertasks.Users.model.DTOs.UserDto;
import com.cbcode.dealertasks.Users.service.AdminUserService;
import com.cbcode.dealertasks.Users.service.impl.DTOsResponses.UserDeletionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*") // This annotation needs
// to be altered to the specific URL of the frontend application that will consume the API in production
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final AdminUserService adminUserService;

    public AdminController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    /**
     * Gets all users.
     * Requires the user to have the 'ROLE_ADMIN' authority.
     * @param page the page number.
     * @param size the number of users per page.
     * @param sortBy the field to sort by.
     * @return a ResponseEntity with a page of users.
     */
    @GetMapping(value = "/all", produces = "application/json")
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(adminUserService.getAllUsers(pageable));
    }

    /**
     * Gets a user by id.
     * Requires the user to have the 'ROLE_ADMIN' authority.
     * @param id the user's id.
     * @return a ResponseEntity with the user.
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.getUserById(id));
    }

    /**
     * Deletes a user by id.
     * Requires the user to have the 'ROLE_ADMIN' authority.
     * @param id the user's id.
     * @return a ResponseEntity with a message.
     */
    @DeleteMapping(value = "/delete/{id}", produces = "application/json")
    public ResponseEntity<UserDeletionResponse> deleteUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.deleteUser(id));
    }

    /**
     * Disables a user by id.
     * Requires the user to have the 'ROLE_ADMIN' authority.
     * @param id the user's id.
     * @return a ResponseEntity with a message.
     */
    @PutMapping(value = "/disable/{id}", produces = "application/json")
    public ResponseEntity<UserDto> disableUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.disableUser(id));
    }

    /**
     * Enables a user by id.
     * Requires the user to have the 'ROLE_ADMIN' authority.
     * @param id the user's id.
     * @return a ResponseEntity with a message.
     */
    @PutMapping(value = "/enable/{id}", produces = "application/json")
    public ResponseEntity<UserDto> enableUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.enableUser(id));
    }

    /**
     * Updates a user by id.
     * Requires the user to have the 'ROLE_ADMIN' authority.
     * @param id the user's id.
     * @param userDto the request body containing the user's updated information.
     * @return a ResponseEntity with the updated user.
     */
    @PutMapping(value = "/admin/update/{id}", produces = "application/json")
    public ResponseEntity<UserDto> adminUpdateUserById(@PathVariable Long id, @RequestBody UserDto userDto) {
        return ResponseEntity.ok(adminUserService.adminUpdateUser(id, userDto));
    }
}
