package com.dekra.availability_manager.service;

import com.dekra.availability_manager.model.CalendarItem;
import com.dekra.availability_manager.model.DTO.CalendarItemDTO;
import com.dekra.availability_manager.model.Employee;
import com.dekra.availability_manager.model.ItemType;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CalendarServiceManagement.class)
@ActiveProfiles("test")
class CalendarServiceManagementTest {

    @Autowired
    private CalendarServiceManagement calendarServiceManagement;
    //Dependencia
    @MockBean
    CalendarService calendarService;
    @MockBean
    EmployeeService employeeService;

    CalendarItemDTO dto = new CalendarItemDTO(null, ItemType.AUSENCIA, "", Instant.parse("2024-07-11T00:00:00Z"), Instant.parse("2024-07-11T23:59:59Z"),"A123456", "");

    @Test
    void test_correct_onInsertCalendar() {
        Employee emp1 = new Employee("A123456","Ezequiel Badía Sánchez", "Big Data", "Malaga", 0, null);
        CalendarItem item1 = new CalendarItem(null, ItemType.AUSENCIA, "", Instant.parse("2024-07-11T00:00:00Z"), Instant.parse("2024-07-11T23:59:59Z"), true, emp1);

        //Given
        when(employeeService.getEmployee(dto.getEmployeeAnumber())).thenReturn(Optional.of(emp1));
        when(calendarService.create(dto,emp1)).thenReturn(item1);

        //When
        List<CalendarItemDTO> newItem = calendarServiceManagement.insertCalendar(dto);

        //Then
        assertEquals(newItem, item1);
    }

    @Test
    @DisplayName("insert employee not found")
    void test_employeeNotFound_onInsertCalendar(){
        when(employeeService.getEmployee("A123456")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> calendarServiceManagement.insertCalendar(dto));
    }
}