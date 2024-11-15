package com.dekra.availability_manager.model.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

    @NotBlank
    private String anumber;

    @NotBlank
    private String name;

    @NotBlank
    private String team;

    @NotBlank
    private String location;

    @NotBlank
    private int holiday;

    private List<CalendarItemDTO> calendarItemDTOS;
}
