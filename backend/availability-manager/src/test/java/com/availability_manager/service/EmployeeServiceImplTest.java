/*package com.availability_manager.service;

import com.availability_manager.model.Employee;
import com.availability_manager.repository.EmployeeRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Disabled
@SpringBootTest(classes = EmployeeServiceImpl.class)
@ActiveProfiles("test")
class EmployeeServiceImplTest {

    @Autowired
    private EmployeeServiceImpl employeeService;

    @MockBean
    private EmployeeRepository employeeRepository;

  /*  @Test
    void test_getAllEmployeesAndItems() {
        //given
        Employee e = new Employee("A123456", "Paco", "Big Data", "Malaga", 0, null);
        List<Employee> employees = new ArrayList<>();
        employees.add(e);

        when(employeeRepository.employeesAndItemActive()).thenReturn(employees);

        //when
        List<Employee> employeesreturn = employeeService.getAllEmployeesAndItems();

        //then
        assertEquals(employees, employeesreturn);
    }*/
/*
    @Test
    void test_getEmployee() {
        //given
        Employee e = new Employee("A123456", "Paco", "Big Data", "Malaga", 0, null);
        when(employeeRepository.findById("A123456")).thenReturn(Optional.of(e));

        //when
        Optional<Employee> employee = employeeService.getEmployee("A123456");

        //then
        assertEquals(e, employee.get());
    }

    @Test
    void test_getEmployeesByDatesRange() {
        Instant startDate = Instant.parse("2021-01-01T00:00:00Z");
        Instant endDate = Instant.parse("2021-01-05T00:00:00Z");
        //given
        Employee e = new Employee("A123456", "Paco", "Big Data", "Malaga", 0, null);
      //  when(employeeRepository.employeesAndItemActiveFilteredByDate(startDate, endDate)).thenReturn(List.of(e));

        //when
     //   List<Employee> employees = employeeService.getEmployeesByDatesRange(startDate, endDate);

        //then
       // assertEquals(e, employees.get(0));
    }

}
*/