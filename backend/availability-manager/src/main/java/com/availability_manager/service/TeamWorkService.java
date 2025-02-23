package com.availability_manager.service;


import com.availability_manager.model.DTO.TeamWorkDTO;
import com.availability_manager.model.TeamWork;

import java.util.List;

public interface TeamWorkService {

    List<TeamWorkDTO> getAllTeamWork();

    TeamWorkDTO getTeamWork(Long id);

    TeamWorkDTO saveTeamWork(TeamWorkDTO TeamWorkDTO);

    TeamWorkDTO updateTeamWork(TeamWorkDTO TeamWorkDTO);

    void deleteTeamWork(Long id);
}
