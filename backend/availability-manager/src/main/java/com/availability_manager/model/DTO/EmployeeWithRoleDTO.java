package com.availability_manager.model.DTO;

import com.availability_manager.model.enumerate.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeWithRoleDTO {
    @NotBlank
    private String anumber;

    @NotBlank
    private String name;

    @NotBlank
    private String team;

    @NotBlank
    private Role role;

    @NotBlank
    private String location;

    @NotBlank
    private int holiday;
}
