package com.dekra.availability_manager.controller;

import com.dekra.availability_manager.model.CalendarItem;
import com.dekra.availability_manager.model.DTO.CalendarItemDTO;
import com.dekra.availability_manager.model.Employee;
import com.dekra.availability_manager.model.ItemType;
import com.dekra.availability_manager.exception.ExistItemException;
import com.dekra.availability_manager.exception.InvalidDateRangeException;
import com.dekra.availability_manager.service.CalendarService;
import com.dekra.availability_manager.service.CalendarServiceManagement;
import jakarta.persistence.EntityNotFoundException;
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

@SpringBootTest(classes = CalendarController.class)
@ActiveProfiles("test")
public class CalendarControllerTest {

    @Autowired
    private CalendarController calendarController;

    @MockBean
    private CalendarService calendarService;
    @MockBean
    private CalendarServiceManagement calendarServiceManagement;

    //variables globales
    Instant inicio = Instant.parse("2024-08-10T00:00:00Z");
    Instant fin = Instant.parse("2024-08-15T00:00:00Z");
    CalendarItemDTO dto = new CalendarItemDTO(1L, ItemType.TELETRABAJO, "", inicio, fin, "A123456", "");
    Employee employee = new Employee("A123456", "nombre", "Big Data", "Malaga", 0, null);
    CalendarItem item = new CalendarItem(1L, ItemType.TELETRABAJO, "", inicio, fin, true, employee);

    @Test
    public void test_correct_getCalendar(){
        List<CalendarItem> calendarItems = new ArrayList<>();

        //give
        when(calendarService.getCalendars(inicio, fin)).thenReturn(new ArrayList<>());
        //when
        ResponseEntity<List<CalendarItem>> response = calendarController.getCalendar(inicio, fin);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(calendarItems, response.getBody());
    }

    @Test
    public void test_correct_updateCalendar(){
        //give
        when(calendarService.updateCalendar(dto, true)).thenReturn(item);

        //when
        ResponseEntity<List<CalendarItemDTO>> response = calendarController.updateCalendar(dto);

        //then
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(item, response.getBody());
    }

    @Test
    public void test_correct_deleteCalendar(){
        //give

        //when
        ResponseEntity<Void> response = calendarController.deleteCalendar(1L);
        //then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void test_correct_addCalendar(){
        //give
        when(calendarServiceManagement.insertCalendar(dto)).thenReturn(List.of(dto));
        //when
        ResponseEntity<List<CalendarItemDTO>> response = calendarController.addCalendar(dto);
        //then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(item, response.getBody());
    }


    //manejadores

    @Test
    public void test_handleException_invalidDateRagne(){
        //give
        InvalidDateRangeException ex = new InvalidDateRangeException("Invalid date range");
        //when
        ResponseEntity<String> response = calendarController.handleException(ex);
        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid date range", response.getBody());
    }

    @Test
    public void test_handleException_entityNotFound(){
        //give
        EntityNotFoundException ex = new EntityNotFoundException("Entity not found");
        //when
        ResponseEntity<String> response = calendarController.handleException(ex);
        //then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Entity not found", response.getBody());
    }

    @Test
    public void test_handleException_existItem(){
        //give
        ExistItemException ex = new ExistItemException("Conflict with existing item");
        //when
        ResponseEntity<String> response = calendarController.handleException(ex);
        //then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Conflict with existing item", response.getBody());
    }
}
