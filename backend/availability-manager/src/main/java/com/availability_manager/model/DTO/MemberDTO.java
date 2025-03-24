package com.availability_manager.model.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {

    @NotBlank
    private String anumber;

    @NotBlank
    private String name;

    @NotBlank
    private String expert; //cambio de nombre de "team" en employee
}
