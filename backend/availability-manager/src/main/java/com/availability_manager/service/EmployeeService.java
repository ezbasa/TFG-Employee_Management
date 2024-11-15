package com.dekra.availability_manager.service;


import com.dekra.availability_manager.model.DTO.EmployeeDTO;
import com.dekra.availability_manager.model.Employee;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Validated
public interface EmployeeService {

    //List<Employee> getAllEmployeesAndItems();

    //usado para calendar-item Management
    Optional<Employee> getEmployee(@NotBlank String anumber);

    List<Employee> getAll();

    List<Employee> getAllByLocation(@NotBlank String location);

    //BBDD CRUD------
    List<EmployeeDTO> getAllEmployeeDTO();

    EmployeeDTO addEmployee(@NotNull EmployeeDTO employeeDTO);

    void deleteEmployee(@NotBlank String anumber);

    EmployeeDTO updateEmployee(@NotNull EmployeeDTO employeeDTO);
}
