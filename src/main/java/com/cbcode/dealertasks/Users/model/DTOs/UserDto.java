package com.cbcode.dealertasks.Users.model.DTOs;

import java.util.Objects;
import java.util.Set;

public class UserDto {
    private Long id;
    private String email;
    private String password;
    private Set<RoleDto> roles;
    private String firstName;
    private String lastName;
    private boolean isEnabled;

    public UserDto() {
    }

    public UserDto(String email, String password, Set<RoleDto> roles, String firstName, String lastName, boolean isEnabled) {
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isEnabled = isEnabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<RoleDto> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleDto> roles) {
        this.roles = roles;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return isEnabled() == userDto.isEnabled()
                && Objects.equals(getId(), userDto.getId())
                && Objects.equals(getEmail(), userDto.getEmail())
                && Objects.equals(getPassword(), userDto.getPassword())
                && Objects.equals(getRoles(), userDto.getRoles())
                && Objects.equals(getFirstName(), userDto.getFirstName())
                && Objects.equals(getLastName(), userDto.getLastName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmail(), getPassword(), getRoles(), getFirstName(), getLastName(), isEnabled());
    }
}
