package com.availability_manager.service;

import com.availability_manager.model.DTO.CalendarItemDTO;
import com.availability_manager.model.Employee;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import com.availability_manager.model.CalendarItem;

import java.time.Instant;
import java.util.List;

@Validated
public interface CalendarService {

    List<CalendarItem> getCalendars(@NotNull Instant fechaInicio, @NotNull Instant fechaFin);

    CalendarItem updateCalendar(@Valid @NotNull CalendarItemDTO dto, @NotNull boolean itemactive);

    void deleteCalendar(@NotNull Long id);

    Instant[] thisyear();

    CalendarItem create(@Valid @NotNull CalendarItemDTO dto, Employee e);

    List<CalendarItem> getBankDays(String Anumber, Instant startDate, Instant endDate);

    List<CalendarItem> getItemsByRangeDate(@NotNull Instant startDate, Instant endDate);

    int getEmployeeHolidays(String anumber);

    void deleteAllItems(String anumber);

    CalendarItem findByIdAndItemActive(long id, boolean itemactive);
}
