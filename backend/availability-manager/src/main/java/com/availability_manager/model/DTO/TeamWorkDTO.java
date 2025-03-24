package com.availability_manager.model.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamWorkDTO {


    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    //@NotNull
    //private String teamLeader; //anumber del proyect_manager

    @NotNull
    private Set<MemberDTO> membersDTOS = new HashSet<>();
}
