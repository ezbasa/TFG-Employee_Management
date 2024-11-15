package com.dekra.availability_manager.service;

import com.dekra.availability_manager.model.CalendarItem;
import com.dekra.availability_manager.model.DTO.CalendarItemDTO;
import com.dekra.availability_manager.model.Employee;
import com.dekra.availability_manager.model.ItemType;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarServiceManagement {

    private final CalendarService calendarService;

    private final EmployeeService employeeService;

    /**
     * Modifica el CalendarItemDTO excluyendo los fines de semana y dias festivos, para ser guardado.
     *  <ul>
     *  *     <li>Si el tipo de item es {@code FESTIVO}, crea un nuevo item para cada empleado en la ubicación
     *  *     especificada por el DTO.</li>
     *  </ul>
     * @param dto
     * @return Lista de CalendarItemsInsertados
     */
    @Transactional
    public List<CalendarItemDTO> insertCalendar(@Valid @NotNull CalendarItemDTO dto) {
        List<CalendarItem> result = new ArrayList<>();

        if(dto.getItemType().equals(ItemType.FESTIVO)){
            List<Employee> employees = employeeService.getAllByLocation(dto.getLocation());

            for(Employee employee : employees){
                dto.setEmployeeAnumber(employee.getAnumber());
                result.add(calendarService.create(dto, employee));
            }

        }else {

            Employee employee = getEmployee(dto.getEmployeeAnumber());

            List<CalendarItemDTO> listItemsDTO = excludeWeekends(dto);
            List<CalendarItemDTO> choppedDTO = excludeBankDays(listItemsDTO);

            if(dto.getItemType().equals(ItemType.VACACIONES)){
                holidayCheck(choppedDTO, 0);
            }

            for (int i = 0; i < choppedDTO.size(); i++) {
                result.add(calendarService.create(choppedDTO.get(i), employee));
            }
        }
        return itemToItemDtoMapper(result);
    }

    //usado en otro sitio(crear una clase mapper
    private List<CalendarItemDTO> itemToItemDtoMapper(List<CalendarItem> items){

        return items.stream()
                .map(calendarItem -> {
                    CalendarItemDTO itemDTO = new CalendarItemDTO();
                    BeanUtils.copyProperties(calendarItem, itemDTO);
                    itemDTO.setEmployeeAnumber(calendarItem.getEmployee().getAnumber());//no lo añade el beanUtils
                    return itemDTO;
                })
                .toList();
    }

    /**
     * Obtiene un empleado por su ID(Anumber)
     * @param Anumber
     * @return Employee
     */
    private Employee getEmployee(String Anumber) {
        return employeeService.getEmployee(Anumber)
                .orElseThrow(()-> new EntityNotFoundException("Employee not found"));
    }

    /**
     * Modifica el CalendarItemDTO excluyendo los fines de semana y dias festivos, para ser guardado.
     * <ul>
     *  <li>Si el ItemType = {@code VACACIONES} hago unas comprobaciones extras</li>
     * </ul>
     * @param dto
     * @return listado dtos
     */
    @Transactional
    public List<CalendarItemDTO> updateCalendar(@Valid @NotNull CalendarItemDTO dto) {

        List<CalendarItemDTO> listItemsDTO = excludeWeekends(dto);
        List<CalendarItemDTO> choppedDTO = excludeBankDays(listItemsDTO);

        if(dto.getItemType().equals(ItemType.VACACIONES)){
            Long daysItem = getHolidayItemToUpdate(dto.getId());
            holidayCheck(choppedDTO, daysItem);
        }

        List<CalendarItem> result = new ArrayList<>();
        boolean itemactive = true;
        for(int i=0; i<choppedDTO.size(); i++){
            result.add(calendarService.updateCalendar(choppedDTO.get(i), itemactive));
            itemactive = false;
        }

        return itemToItemDtoMapper(result);
    }

    /**
     * Obtiene la duración en dias del item original.
     * @param id
     * @return numero de días
     */
    private Long getHolidayItemToUpdate(Long id){
        CalendarItem i = calendarService.findByIdAndItemActive(id, true);
        return ChronoUnit.DAYS.between(i.getStartDate(), i.getEndDate()) + 1;
    }

    /**
     * Comprueba que no se exceda en la selección de vacaciones disponibles.
     * @param listDTO
     * @param originalHoliday
     */
    private void holidayCheck(List<CalendarItemDTO> listDTO, long originalHoliday){
        long newHoliday = 0;

        for(CalendarItemDTO dto : listDTO) {
            newHoliday += ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1;
        }//Sumo 1 día porque al quitarle 1 segundo de cada evento (para que no se pisen la hora inicio y fin) cuenta un día menos

        int spentHoliday = calendarService.getEmployeeHolidays(listDTO.get(0).getEmployeeAnumber());
        int totalHoliday = getEmployee(listDTO.get(0).getEmployeeAnumber()).getHoliday();

        long finalHoliday = totalHoliday - ((newHoliday-originalHoliday) + spentHoliday); //+ originalHoliday;

        if(finalHoliday < 0){
            throw new ValidationException("holidays overcame");
        }
    }

    /**
     * Divide un CalendarItemDTO en una lista, haciendo un split por cada
     * fin de semana (sábado y domingo) que se encuentre dentro del rango
     * de la fecha de inicio y la fecha de fin del DTO recibido por parámetro.
     * @param dto
     * @return listado de items (excluyendo fines de semanada)
     */
    private List<CalendarItemDTO> excludeWeekends(CalendarItemDTO dto) {

        List<CalendarItemDTO> choppedDTO = new ArrayList<>();
        long iterations = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate());
        ZonedDateTime startZonedDate = dto.getStartDate().atZone(ZoneId.systemDefault());

        CalendarItemDTO newDTO = packagingNewDto(dto);
        Instant fechaInicio = null;

        int i=0;
        while (i <= iterations) {
            ZonedDateTime nextZonedDate = startZonedDate.plusDays(i);

            if (fechaInicio == null && !isSaturday(nextZonedDate)) {
                fechaInicio = nextZonedDate.toInstant();
            }

            if ((isSaturday(nextZonedDate) || (i == iterations)) && fechaInicio != null) {
                newDTO.setStartDate(fechaInicio);

                if(i == iterations && !isSaturday(nextZonedDate)) {
                    newDTO.setEndDate(dto.getEndDate());
                }else {
                    newDTO.setEndDate(nextZonedDate.minusSeconds(1).toInstant());
                }

                choppedDTO.add(newDTO);

                newDTO = packagingNewDto(dto);
                fechaInicio = null;

                i++;
            }
            i++;
        }
        return choppedDTO;
    }

    /**
     * Recibe una lista de CalendarItemDTO y hace un split por cada día festivo
     * que se encuentre dentro del rango de la fecha de inicio y la fecha de fin
     * del DTO correspondiente.
     * @param dtos
     * @return lista de CalendarItemDTOS
     */
    private List<CalendarItemDTO> excludeBankDays(List<CalendarItemDTO> dtos) {
        List<CalendarItemDTO> choppedDTO = new ArrayList<>();

        for(CalendarItemDTO dto : dtos) {

            List<CalendarItem> itemsBankDays = calendarService.getBankDays(dto.getEmployeeAnumber(), dto.getStartDate(), dto.getEndDate());

            if(!itemsBankDays.isEmpty()){

                long dtoDays = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1;
                ZonedDateTime itemStartZoneDate = dto.getStartDate().atZone(ZoneId.systemDefault());
                ZonedDateTime bankStartZonedDate = itemsBankDays.get(0).getStartDate().atZone(ZoneId.systemDefault());

                CalendarItemDTO newDTO = packagingNewDto(dto);
                int iterationDTO;
                int iterationBankDays = 0;

                for(iterationDTO=0; iterationDTO < dtoDays; iterationDTO++){

                    if((iterationBankDays < itemsBankDays.size()) && itemStartZoneDate.plusDays(iterationDTO).equals(bankStartZonedDate)) {

                        if(newDTO.getStartDate() != null){
                            newDTO.setEndDate(itemStartZoneDate.plusDays(iterationDTO).minusSeconds(1).toInstant());

                            choppedDTO.add(newDTO);

                            if(iterationDTO < dtoDays-1)
                                newDTO = packagingNewDto(dto);
                        }

                        iterationBankDays++;
                        if((iterationBankDays < itemsBankDays.size())){
                            bankStartZonedDate = itemsBankDays.get(iterationBankDays).getStartDate().atZone(ZoneId.systemDefault());
                        }

                    }else{

                        if(newDTO.getStartDate() == null){
                            newDTO.setStartDate(itemStartZoneDate.plusDays(iterationDTO).toInstant());
                        }
                    }
                }
                if(newDTO.getEndDate() == null){
                    newDTO.setEndDate(itemStartZoneDate.plusDays(iterationDTO).minusSeconds(1).toInstant());

                    choppedDTO.add(newDTO);
                }
            }else{
                choppedDTO.add(dto);
            }
        }
        return choppedDTO;
    }

    /**
     * Crea un nuevo objeto DTO para solo rellenar las fechas
     * @param dto
     * @return nuevo calendarItemDTO
     */
    private CalendarItemDTO packagingNewDto(CalendarItemDTO dto){
        CalendarItemDTO newDTO = new CalendarItemDTO();

        newDTO.setId(dto.getId());//para update
        newDTO.setEmployeeAnumber(dto.getEmployeeAnumber());
        newDTO.setDescription(dto.getDescription());
        newDTO.setItemType(dto.getItemType());
        newDTO.setLocation(dto.getLocation());

        return newDTO;
    }

    /**
     *  Verifica si el día es sábado
     * @param date
     * @return {@code TRUE} si es sabado
     */
    private boolean isSaturday(ZonedDateTime date) {
        return date.getDayOfWeek().getValue() == 6 || date.getDayOfWeek().getValue() == 7;
    }
}