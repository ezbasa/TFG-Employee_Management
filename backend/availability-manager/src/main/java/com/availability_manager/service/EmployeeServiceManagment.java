package com.dekra.availability_manager.service;

import com.dekra.availability_manager.model.CalendarItem;
import com.dekra.availability_manager.model.DTO.CalendarItemDTO;
import com.dekra.availability_manager.model.DTO.EmployeeDTO;
import com.dekra.availability_manager.model.Employee;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceManagment {

    private final CalendarService calendarService;

    private final EmployeeService employeeService;

    /**
     * Obtengo los empleados(transformo DTO), obtengo los items(transformo a DTO)
     * y enlazo cada lista de items a su empleado
     * @param startDate
     * @param endDate
     * @return Lista de empleados con su respectiva lista de items
     */
    public List<EmployeeDTO> getEmployeesByDatesRange(@NotNull Instant startDate, Instant endDate){

        List<EmployeeDTO> employeesDTOS = employeeToEmployeeDtoMapper(employeeService.getAll());
        List<CalendarItemDTO> itemDTOS = itemToItemDtoMapper(calendarService.getItemsByRangeDate(startDate, endDate));

        //Agrupo todos los items de un empleado y se los enlazo
        for(EmployeeDTO emp : employeesDTOS){
            List<CalendarItemDTO> itemsFiltered = itemDTOS.stream()
                    .filter(item -> item.getEmployeeAnumber().equals(emp.getAnumber()))
                    .toList();

            emp.setCalendarItemDTOS(itemsFiltered);
        }

        addAvailableHolidays(employeesDTOS);

        return employeesDTOS;
    }

    /**
     * Mapeado de Employee a EmployeeDTO
     * @param employees
     * @return Lista de EmployeeDTO
     */
    private List<EmployeeDTO> employeeToEmployeeDtoMapper(List<Employee> employees){

        return employees.stream()
                .map(employee -> {
                    EmployeeDTO empDTO = new EmployeeDTO();
                    BeanUtils.copyProperties(employee, empDTO);
                    return empDTO;
                })
                .toList();
    }

    /**
     * Mapeado de CalendarItem a CalendarItemDTO
     * @param items
     * @return Lista de CalendarItemDTO
     */
    private List<CalendarItemDTO> itemToItemDtoMapper(List<CalendarItem> items){

        return items.stream()
                .map(calendarItem -> {
                    CalendarItemDTO itemDTO = new CalendarItemDTO();
                    BeanUtils.copyProperties(calendarItem, itemDTO);
                    itemDTO.setEmployeeAnumber(calendarItem.getEmployee().getAnumber());
                    return itemDTO;
                })
                .toList();
    }

    /**
     * Rellena el campo de holiday de cada empleado con las vacaciones restantes.
     * @param employeesDTOS
     */
    private void addAvailableHolidays(List<EmployeeDTO> employeesDTOS) {
        int holidays;

        for(EmployeeDTO emp : employeesDTOS){
            holidays = calendarService.getEmployeeHolidays(emp.getAnumber());
            emp.setHoliday(emp.getHoliday() - holidays);//fallo en getholiday (null)
        }
    }

    /**
     * Elimina los items del empleado y seguidamente al empleado
     * @param anumber
     */
    public void deleteEmployee(@NotBlank String anumber){
        calendarService.deleteAllItems(anumber);
        employeeService.deleteEmployee(anumber);
    }
}
