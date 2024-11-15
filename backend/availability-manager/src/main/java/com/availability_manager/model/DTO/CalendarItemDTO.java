package com.dekra.availability_manager.model.DTO;


import com.dekra.availability_manager.model.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarItemDTO {

    private Long id; //campo incluido para update (item-calendar)

    @NotNull
    private ItemType itemType;

    private String description;

    @NotNull
    private Instant startDate;

    @NotNull
    private Instant endDate;

    @NotBlank
    private String employeeAnumber;

    private String location;
}
