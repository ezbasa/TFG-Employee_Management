package com.availability_manager.service;

import com.availability_manager.model.CalendarItem;
import com.availability_manager.model.DTO.CalendarItemDTO;
import com.availability_manager.model.DTO.EmployeeDTO;
import com.availability_manager.model.DTO.EmployeeWithRoleDTO;
import com.availability_manager.model.Employee;
import com.availability_manager.model.Login;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceManagment {

    private final CalendarService calendarService;

    private final EmployeeService employeeService;

    private final AuthService authService;

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

    public List<EmployeeWithRoleDTO> employeeToEmployeeWithRoleDtoMapper(){
        List<Employee> employeeList = employeeService.getAll();
        List<Login> loginList = authService.getAllLogins();

        return employeeList.stream()
                .map(employee -> {
                    EmployeeWithRoleDTO dto = new EmployeeWithRoleDTO();

                    //datos generales del employee
                    BeanUtils.copyProperties(employee, dto);

                    //añadimos el login
                    loginList.stream()
                            .filter(login -> login.getEmployeeAnumber().equals(employee.getAnumber()))
                            .findFirst()
                            .ifPresent(login -> dto.setRole(login.getRole()));

                    return dto;
                })
                .toList();
    }

    @Transactional
    public EmployeeWithRoleDTO updateEmployee(@NotNull EmployeeWithRoleDTO employeeRoleDTO) {
        //actualizo empleado
        EmployeeDTO employeeDTO = new EmployeeDTO();
        BeanUtils.copyProperties(employeeRoleDTO, employeeDTO);
        employeeService.updateEmployee(employeeDTO);

        //actualizo rol
        Login login = new Login();
        login.setEmployeeAnumber(employeeRoleDTO.getAnumber());
        login.setRole(employeeRoleDTO.getRole());
        authService.updateLogin(login);

        return employeeRoleDTO;
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
