package com.availability_manager.service;

import com.availability_manager.model.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceManagement {

    private final EmployeeService employeeService;

    public Optional<Employee> getEmployee(String anumber){
        return employeeService.getEmployee(anumber);
    }
}
