package com.availability_manager.controller;

import com.availability_manager.model.DTO.EmployeeDTO;
import com.availability_manager.model.Employee;
import com.availability_manager.service.EmployeeService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Disabled
@SpringBootTest(classes = EmployeeController.class)
@ActiveProfiles("test")
public class EmployeeControllerTest {

    @Autowired
    private EmployeeController employeeController;

    @MockBean
    private EmployeeService employeeService;

   /* @Test
    public void test_correct_getAll(){
        //give
        when(employeeService.getAllEmployeesAndItems()).thenReturn(new ArrayList<Employee>());
        //when
        ResponseEntity<List<Employee>> response = employeeController.getAll();
        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().size() == 0);
    }*/

   /* @Test
    public void test_correct_getEmployeesByDatesRange(){
        Instant startDate = Instant.parse("2021-01-01T00:00:00Z");
        Instant endDate = Instant.parse("2021-01-05T00:00:00Z");

        //give
        when(employeeService.getEmployeesByDatesRange(startDate, endDate)).thenReturn(new ArrayList<Employee>());
        //when
        ResponseEntity<List<Employee>> response = employeeController.getRange(startDate, endDate);
        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().size() == 0);
    }*/
}
