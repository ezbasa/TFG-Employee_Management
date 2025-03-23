package com.availability_manager.service;

import com.availability_manager.config.ConfigTeamWork;
import com.availability_manager.exception.MaxTeamsReachedException;
import com.availability_manager.exception.NoMinimumMembersException;
import com.availability_manager.model.DTO.MemberDTO;
import com.availability_manager.model.DTO.TeamWorkDTO;
import com.availability_manager.model.Employee;
import com.availability_manager.model.TeamWork;
import com.availability_manager.repository.TeamWorkRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class TeamWorkServiceImpl implements TeamWorkService {

    private final TeamWorkRepository teamWorkRepository;

    private final TeamWorkServiceManagement teamWorkManagement;

    private final ConfigTeamWork memberMaxTeam;

    @Override
    public List<TeamWorkDTO> getAllTeamWork() {
        List<TeamWork> teamWorkList = teamWorkRepository.findAll();
        List<TeamWorkDTO> teamWorkDTOList = new ArrayList<>();

        teamWorkList.forEach(teamWork -> {teamWorkDTOList.add(teamWorkMapToDTO(teamWork));});

        return teamWorkDTOList;
    }

    @Override
    public TeamWorkDTO getTeamWork(Long id) {
        TeamWork t = teamWorkRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TeamWork not found"));

        return teamWorkMapToDTO(t);
    }

    @Override
    public TeamWorkDTO saveTeamWork(TeamWorkDTO teamWorkDTO) {

        if(teamWorkDTO.getMembersDTOS().size() < 2){
            throw new NoMinimumMembersException("The minimum members cannot be less than 2");
        }

        CheckMember(teamWorkDTO.getMembersDTOS());

        TeamWork teamWork = teamWorkDtoMapToTeamWork(teamWorkDTO);
        TeamWork t = teamWorkRepository.save(teamWork);

        return teamWorkMapToDTO(t);
    }

    @Override
    public TeamWorkDTO updateTeamWork(TeamWorkDTO teamWorkDTO) {

        TeamWork t = teamWorkRepository.findById(teamWorkDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("TeamWork not found"));

        if(teamWorkDTO.getMembersDTOS().size() < 2){
            throw new NoMinimumMembersException("The minimum members cannot be less than 2");
        }

        CheckUpdate(t,teamWorkDTO);

        //copio las propiedades
        t = teamWorkDtoMapToTeamWork(teamWorkDTO);
        TeamWork twSave = teamWorkRepository.save(t);
        return teamWorkMapToDTO(twSave);
    }

    @Override
    public void deleteTeamWork(Long id) {
        teamWorkRepository.deleteById(id);
    }

    @Override
    public void deleteMemberTeamwork(String anumber) {
        List<TeamWork> teamWorkList = teamWorkRepository.membersInTeamWork(anumber);

        teamWorkList.forEach(teamWork -> {
            Set<Employee> setEmployee = teamWork.getEmployees();
            setEmployee.removeIf(employee -> employee.getAnumber().equals(anumber));
            teamWorkRepository.save(teamWork);
        });
    }

    //FUNCIONES
    private TeamWork teamWorkDtoMapToTeamWork(TeamWorkDTO teamWorkDTO) {
        TeamWork teamWork = new TeamWork();
        BeanUtils.copyProperties(teamWorkDTO, teamWork);
        teamWork.setEmployees(teamWorkManagement.getEmployees(teamWorkDTO.getMembersDTOS()));

        return teamWork;
    }

    /**
     * mapea al dto
     * @param teamWork
     * @return
     */
    private TeamWorkDTO teamWorkMapToDTO(TeamWork teamWork) {
        TeamWorkDTO teamWorkDTO = new TeamWorkDTO();

        //copio propiedades generales
        BeanUtils.copyProperties(teamWork, teamWorkDTO);

        //copio los miembros
        Set<MemberDTO> memberDTOSet = new HashSet<>();
        teamWork.getEmployees().forEach(member -> {
            MemberDTO memberDTO = new MemberDTO();
            BeanUtils.copyProperties(member, memberDTO);
            memberDTO.setExpert(member.getTeam());

            memberDTOSet.add(memberDTO);
        });

        teamWorkDTO.getMembersDTOS().addAll(memberDTOSet);

        return teamWorkDTO;
    }

    /**
     * Comrpueba si los empleados han sido cambiados, en ese caso chequea al los empleados
     * @param t
     * @param teamWorkDTO
     */
    private void CheckUpdate(TeamWork t, TeamWorkDTO teamWorkDTO){

        if(t.getEmployees().size() == teamWorkDTO.getMembersDTOS().size()) {
            //NONEMATCH devuelve true cuando alguno no coincide y el ANYMATCH deveulve true y recibe algún true
            //si un miembor no coincide, directamente se va a comprobar si el miembro puede o no estar en mas grupos
            boolean employeesChanged = t.getEmployees().stream().anyMatch(employee ->
                    teamWorkDTO.getMembersDTOS().stream().noneMatch(memberDTO ->
                            employee.getAnumber().equals(memberDTO.getAnumber())
                    )
            );

            if (employeesChanged) {
                CheckMember(teamWorkDTO.getMembersDTOS());
            }
        }
    }

    /**
     * Comprueba que el empleado no esté en más grupo de los definidos en properties
     * @param members
     */
    private void CheckMember(Set<MemberDTO> members){

        members.forEach(member -> {
            //obtengo la lista de grupos donde se encuntra un usuario
            List<TeamWork> teamWorkList = teamWorkRepository.membersInTeamWork(member.getAnumber());
            if(teamWorkList.size() >= memberMaxTeam.getMaxTeams()+1){
                throw new MaxTeamsReachedException("The employee cannot be in more teams");
            }
        });
    }

    private void minimumMembers(TeamWorkDTO teamWorkDTO){



    }
}

/*
LOGICA A IMPLEMENTAR
- comprobación en cuantos grupos se encuentra el empleado
    *query que traiga todos los grupo en los que aparece un empleado
- creación de grupo autámática ---------(no lo veo a implementar en el back)---------------

- Que en todos los grupos existan todos los compoennetes ??
 */