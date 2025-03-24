package com.availability_manager.service;

import com.availability_manager.model.DTO.MemberDTO;
import com.availability_manager.model.Employee;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class TeamWorkServiceManagement {

    private final EmployeeService employeeService;

    public Set<Employee> getEmployees(Set<MemberDTO> members) {
        Set<Employee> employees = new HashSet<>();

        for (MemberDTO member : members) {
            Employee e = employeeService.getEmployee(member.getAnumber())
                    .orElseThrow(()->new EntityNotFoundException("Entity not found"));

            employees.add(e);
        }

        return employees;
    }

    public List<MemberDTO> getAllMembers() {
        List<MemberDTO> members = new ArrayList<>();
        List<Employee> employeeList = employeeService.getAll();

        employeeList.forEach(employee -> {
            MemberDTO memberDTO = employeeMapperToMemberDTO(employee);
            members.add(memberDTO);
        });

        return members;
    }

    //mapper de membersDTO
    private MemberDTO employeeMapperToMemberDTO(Employee employee) {
        MemberDTO memberDTO = new MemberDTO();

        memberDTO.setAnumber(employee.getAnumber());
        memberDTO.setName(employee.getName());
        memberDTO.setExpert(employee.getTeam());

        return memberDTO;
    }
}
