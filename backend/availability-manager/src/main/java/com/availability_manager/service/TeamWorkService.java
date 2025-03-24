package com.availability_manager.service;


import com.availability_manager.model.DTO.TeamWorkDTO;
import com.availability_manager.model.TeamWork;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface TeamWorkService {

    List<TeamWorkDTO> getAllTeamWork();

    TeamWorkDTO getTeamWork(@NotBlank Long id);

    TeamWorkDTO saveTeamWork(@NotNull TeamWorkDTO TeamWorkDTO);

    TeamWorkDTO updateTeamWork(@NotNull TeamWorkDTO TeamWorkDTO);

    void deleteTeamWork(@NotBlank Long id);

    void deleteMemberTeamwork(@NotBlank String anumber);
}
