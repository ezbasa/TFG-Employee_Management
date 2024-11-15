package com.dekra.availability_manager.controller;

import com.dekra.availability_manager.model.CalendarItem;
import com.dekra.availability_manager.model.DTO.CalendarItemDTO;
import com.dekra.availability_manager.model.Employee;
import com.dekra.availability_manager.model.ItemType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CalendarControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Test
    public void testAddCalendar_corect() throws Exception {
        //anumber diferente al de la bbdd
        CalendarItemDTO dto = new CalendarItemDTO(null, ItemType.TELETRABAJO, "", Instant.parse("2024-07-18T00:00:00Z"), Instant.parse("2024-07-18T23:59:59Z"), "A345678", "");
        String jsonDto = jacksonObjectMapper.writeValueAsString(dto);

        Employee e = new Employee("A345678", "Antonio Pérez Farfan", "IA", "Malaga", 0,null);
        CalendarItem item = new CalendarItem(null, ItemType.TELETRABAJO, "", Instant.parse("2024-07-18T00:00:00Z"), Instant.parse("2024-07-18T23:59:59Z"), true, e);
        String jsonItem = jacksonObjectMapper.writeValueAsString(item);

        //create correcto
        MvcResult result = mockMvc.perform(post("/item-calendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDto))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        //convertir json en objectnode, para poder manipularlo (para poder hacer el assert, no dispongo del id que la bbdd ponga)
        String jsonItemrecived = result.getResponse().getContentAsString();
        ObjectNode nodeitemreceived = (ObjectNode) jacksonObjectMapper.readTree(jsonItemrecived);
        nodeitemreceived.set("id", null);
        jsonItemrecived = jacksonObjectMapper.writeValueAsString(nodeitemreceived);

        assertEquals(jsonItem, jsonItemrecived);
    }

    @Test
    public void testDeleteCalendar_correct() throws Exception {
        mockMvc.perform(delete("/item-calendar")
                .param("id","34"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateCalendar_correct() throws Exception {
        //anumber diferente al de la bbdd
        CalendarItemDTO dto = new CalendarItemDTO(3L, ItemType.TELETRABAJO, "", Instant.parse("2026-07-18T00:00:00Z"), Instant.parse("2026-07-18T23:59:59Z"), "A234567", "");
        String jsonDto = jacksonObjectMapper.writeValueAsString(dto);

        Employee e = new Employee("A234567", "Isaias Vazquez Nuñez", "Big Data", "Malaga", 0, null);
        CalendarItem item = new CalendarItem(null, ItemType.TELETRABAJO, "", Instant.parse("2026-07-18T00:00:00Z"), Instant.parse("2026-07-18T23:59:59Z"), true, e);
        String jsonItem = jacksonObjectMapper.writeValueAsString(item);

        //create correcto
        MvcResult result = mockMvc.perform(put("/item-calendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDto))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        //convertir json en objectnode, para poder manipularlo (para poder hacer el assert, no dispongo del id que la bbdd ponga)
        String jsonItemrecived = result.getResponse().getContentAsString();
        ObjectNode nodeitemreceived = (ObjectNode) jacksonObjectMapper.readTree(jsonItemrecived);
        nodeitemreceived.set("id", null);
        jsonItemrecived = jacksonObjectMapper.writeValueAsString(nodeitemreceived);

        assertEquals(jsonItem, jsonItemrecived);
    }

}
