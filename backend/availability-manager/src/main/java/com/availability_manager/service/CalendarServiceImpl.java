package com.availability_manager.service;

import com.availability_manager.model.CalendarItem;
import com.availability_manager.model.DTO.CalendarItemDTO;
import com.availability_manager.model.Employee;
import com.availability_manager.model.enumerate.ItemType;
import com.availability_manager.exception.ExistItemException;
import com.availability_manager.exception.InvalidDateRangeException;
import com.availability_manager.repository.CalendarRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.time.ZoneOffset.UTC;

@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {

    private final CalendarRepository repository;

    /**
     * Obtiene una lista de CalendarItem dentro de un rango de fechas
     * @param fechaInicio
     * @param fechaFin
     * @return Lista de CalendarItem
     */
    @Override
    public List<CalendarItem> getCalendars(@NotNull Instant fechaInicio, @NotNull Instant fechaFin) {
        dateChecks(fechaInicio, fechaFin);

        return repository.findByStartDateBetweenAndItemActive(fechaInicio, fechaFin, true);
    }

    /**
     * Si el CalendarITem existe, cambia el valor de {@code itemActive} a (false)
     * <ul>
     *     <il>Si el ItemType es {@code BANKDAY}, elimina todos los que coincidan con la fecha de inicio y fecha fin</il>
     * </ul>
     * @param id
     */
    @Override
    public void deleteCalendar(@NotNull Long id) {
        CalendarItem item = findByIdAndItemActive(id, true);

        if(item.getItemType().equals(ItemType.BANKDAY)){
            repository.falseAllBankDays(item.getStartDate(), item.getEndDate());

        }else {

            item.setItemActive(false);
            repository.save(item);
        }
    }

    /**
     * Guarda en bbdd el CalendatItem
     * @param dto
     * @param e
     * @return CalendarItem
     */
    @Transactional
    public CalendarItem create(@Valid @NotNull CalendarItemDTO dto, Employee e) {
        this.createChecks(dto);

        CalendarItem nuevoitem = new CalendarItem();
        BeanUtils.copyProperties(dto, nuevoitem);
        nuevoitem.setEmployee(e);
        nuevoitem.setItemActive(true);

        return repository.save(nuevoitem);
    }

    /**
     * Guarda en bbdd el CalendatItem
     * @param dto
     * @param itemactive
     * @return CalendarItem
     */
    @Override
    @Transactional
    public CalendarItem updateCalendar(@Valid @NotNull CalendarItemDTO dto, boolean itemactive) {
        Employee e = updateChecks(dto, itemactive);

        CalendarItem updateItem = new CalendarItem();
        BeanUtils.copyProperties(dto, updateItem);
        updateItem.setEmployee(e);
        updateItem.setId(null);
        updateItem.setItemActive(true);

        return repository.save(updateItem);
    }

    /**
     * Obtiene los CalendarItem festivos de un empleado en un rango de fechas
     * @param Anumber
     * @param startDate
     * @param endDate
     * @return Lista de CalendarItem (ItemType={@code BankDay})
     */
    @Override
    public List<CalendarItem> getBankDays(String Anumber, Instant startDate, Instant endDate) {
        return repository.findEmployeeItems(Anumber, ItemType.BANKDAY, startDate, endDate);
    }

    /**
     * Obtiene los CalendarItem en un rango de fechas
     * @param startDate
     * @param endDate
     * @return Lista de CalendarItem
     */
    @Override
    public List<CalendarItem> getItemsByRangeDate(Instant startDate, Instant endDate){
        dateChecks(startDate, endDate);

        return repository.getItemsByRangeDate(startDate, endDate);
    }

    /**
     * Calcula los días de vacaciones anuales que tiene seleccionado un empleado
     * @param anumber
     * @return vacaciones ya reservadas
     */
    @Override
    public int getEmployeeHolidays(String anumber){
        int holidays = 0;
        int bankHolidays = 0;

        Instant[] thisyear = thisyear();

        List<CalendarItem> holidaysItems = repository.findEmployeeItems(anumber, ItemType.HOLIDAY, thisyear[0], thisyear[1]);

        for(CalendarItem item : holidaysItems) {
            holidays += (int)ChronoUnit.DAYS.between(item.getStartDate(), item.getEndDate()) + 1;
            //Sumo 1 día porque al quitarle 1 segundo de cada evento (para que no se pisen la hora inicio y fin) cuenta un día menos

            bankHolidays += repository.findEmployeeItems(anumber, ItemType.BANKDAY, item.getStartDate(), item.getEndDate()).size();
        }

        return holidays-bankHolidays;
    }

    /**
     * Obtiene la fecha del año actual
     * @return Array de fechas (Instant
     */
    @Override
    public Instant[] thisyear() {
        Instant[] thisyear = new Instant[2];

        LocalDateTime currentDateTime = LocalDateTime.now();
        ZoneId zoneId = ZoneId.systemDefault();

        LocalDateTime startOfYear = LocalDateTime.of(currentDateTime.getYear(), Month.JANUARY, 1, 0, 0, 0);
        thisyear[0] = startOfYear.atZone(zoneId).toInstant();

        LocalDateTime endOfYear = LocalDateTime.of(currentDateTime.getYear(), Month.DECEMBER, 31, 23, 59, 59);
        thisyear[1] = endOfYear.toInstant(UTC);

        return thisyear;
    }

    /**
     * Borra TODOS los items de un empleado.
     * @param anumber
     */
    @Override //borrar items cuando borras un empleado
    public void deleteAllItems(String anumber){
        repository.deleteAllByAnumber(anumber);
    }


    /**
     * Comprueba que el CalendarItem a añadir no coincide con uno existente. (mediante sus datos)
     * @param dto
     */
    private void createChecks(@NotNull CalendarItemDTO dto) {
        dateChecks(dto.getStartDate(), dto.getEndDate());

        if(repository.existsByStartDateAndEndDateAndEmployee_AnumberAndItemActiveAndItemType(dto.getStartDate(),dto.getEndDate(), dto.getEmployeeAnumber(), true, dto.getItemType())){

            throw new ExistItemException("Item already exists");
        }

        overlapChecks(dto);
    }

    /**
     * Comprobaciones básicas para la actualización de un CalendarItem
     * @param dto
     * @param itemactive
     * @return Employee
     */
    private Employee updateChecks(CalendarItemDTO dto, boolean itemactive) {
        dateChecks(dto.getStartDate(), dto.getEndDate());

        CalendarItem oldItem = findByIdAndItemActive(dto.getId(), itemactive);

        oldItem.setItemActive(false);
        repository.save(oldItem);

        overlapChecks(dto);

        return oldItem.getEmployee();
    }

    /**
     * Busca un CalendarItem por su Id e ItemActive=TRUE
     * @param id
     * @param itemactive
     * @return CalendarItem
     */
    @Override
    public CalendarItem findByIdAndItemActive(long id, boolean itemactive) {
        return repository.findByIdAndItemActive(id, itemactive)
                .orElseThrow(() -> new EntityNotFoundException("ID item not found"));
    }

    /**
     * Comprobaciones para que los CalendarItem no se solapen
     * <ul>
     *     <il>Comprueba que dentro del rango de fechas del nuevo CalendarItem no comience un CalendarItem existente</il>
     *     <il>Comprueba que dentro del rango de fechas del nuevo CalendarItem no termine un CalendarItem existente</il>
     *     <il>Comprueba si el nuevo CalendarItem se encuentre dentro del rango de fechas de un CalendarItem existente</il>
     * </ul>
     * @param dto
     */
    private void overlapChecks(CalendarItemDTO dto){

        if(repository.startDateBetweenThisItem(dto.getEmployeeAnumber(), true, dto.getStartDate(), dto.getEndDate(), dto.getItemType().getCompatibles()) ||
           repository.endDateBetweenThisItem(dto.getEmployeeAnumber(), true, dto.getStartDate(), dto.getEndDate(), dto.getItemType().getCompatibles()) ||
           repository.startDateBeforeAndEndDateAfterThisItem(dto.getEmployeeAnumber(), true, dto.getStartDate(), dto.getEndDate(), dto.getItemType().getCompatibles())
        ){
            throw new ExistItemException("Conflict with existing Item");
        }
    }

    /**
     * Comprueba que la fecha de inicio no sea más tarde que la fecha fin
     * @param startDate
     * @param endDate
     */
    private void dateChecks(Instant startDate, Instant endDate){
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("Start date must be before end date");
        }
    }

}