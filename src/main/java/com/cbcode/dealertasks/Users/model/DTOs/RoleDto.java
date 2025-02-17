package com.cbcode.dealertasks.Users.model.DTOs;

import com.cbcode.dealertasks.Users.model.Enums.EnumRole;

import java.util.Objects;

public class RoleDto {
    private Long id;
    private EnumRole name;

    public RoleDto() {
    }

    public RoleDto(Long id, EnumRole name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public EnumRole getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(EnumRole name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RoleDto roleDto = (RoleDto) o;
        return Objects.equals(getId(), roleDto.getId()) && getName() == roleDto.getName();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }
}
