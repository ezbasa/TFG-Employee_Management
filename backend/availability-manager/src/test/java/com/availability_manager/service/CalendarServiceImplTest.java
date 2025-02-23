/*package com.availability_manager.service;

import com.availability_manager.model.CalendarItem;
import com.availability_manager.model.DTO.CalendarItemDTO;
import com.availability_manager.model.Employee;
import com.availability_manager.model.enumerate.ItemType;
import com.availability_manager.exception.ExistItemException;
import com.availability_manager.exception.InvalidDateRangeException;
import com.availability_manager.repository.CalendarRepository;
import com.availability_manager.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Disabled
@SpringBootTest(classes = CalendarServiceImpl.class)
@ActiveProfiles("test")
class CalendarServiceImplTest {

    @Autowired
    CalendarServiceImpl calendarService;

    @MockBean
    CalendarRepository calendarRepository;

    //variables
    Instant startDate = Instant.parse("2020-01-01T00:00:00Z");
    Instant endDate = Instant.parse("2020-12-31T00:00:00Z");
    private EmployeeRepository employeeRepository;


    @Test
    void test_correct_onGetCalendars() {
        //Datos
        Employee emp1 = new Employee("A123458", "Cristian Mena Acedo", "Big Data", "Malaga", 0, null);
        Employee emp2 = new Employee("A123459", "Jorge Alcaraz Bravo", "Big Data", "Malaga" , 0, null);

        CalendarItem item1 = new CalendarItem(1L, ItemType.ABSENCE, "Personal", Instant.parse("2024-07-11T00:00:00Z"), Instant.parse("2024-07-11T23:59:59Z"), true, emp1);
        //CalendarItem item2 = new CalendarItem(2L, ItemType.HOLIDAYS, "", LocalDate.of(2024,07, 11), LocalDate.of(2024, 07, 11), false, emp2);
        CalendarItem item3 = new CalendarItem(3L, ItemType.TELEWORK, "", Instant.parse("2024-07-11T00:00:00Z"), Instant.parse("2024-07-11T23:59:59Z"), true, emp2);
        CalendarItem item4 = new CalendarItem(4L, ItemType.TELEWORK, "", Instant.parse("2024-07-11T00:00:00Z"), Instant.parse("2024-07-11T23:59:59Z"), true, emp1);

        List<CalendarItem> listitems = List.of(item1, item3, item4); //solo los items activos

        //given
        when(calendarRepository.findByStartDateBetweenAndItemActive(startDate, endDate, true)).thenReturn(listitems);

        //when
        List<CalendarItem> allitems = calendarService.getCalendars(startDate, endDate);

        //then
        assertNotNull(allitems);
        assertEquals(3, allitems.size());
        assertEquals(item1, allitems.get(0));
    }

    @Test
    void test_invalidDates_onGetCalendars() {
        Instant startDate2 = Instant.parse("2025-01-01T00:00:00Z");
        Instant endDate2 = Instant.parse("2021-01-01T00:00:00Z");

        //given
        //when
        assertThrows(InvalidDateRangeException.class, () -> calendarService.getCalendars(startDate2, endDate2));
        //then
    }

    //variable global
    CalendarItemDTO dto = new CalendarItemDTO(1L, ItemType.TELEWORK, "", startDate, endDate, "A123458", "");

    @Test
    void test_correct_onUpdateCalendar() {
        //Datos


        Employee emp1 = new Employee("A123458", "Cristian Mena Acedo", "Big Data", "Malaga", 0, null);
        CalendarItem olditem = new CalendarItem(1L, ItemType.TELEWORK, "", Instant.parse("2024-07-11T00:00:00Z"), Instant.parse("2024-07-11T23:59:59Z"), true, emp1);
        CalendarItem newitem = new CalendarItem(null, ItemType.TELEWORK, "", startDate, endDate, true, emp1);

        //given
        //updateChecks()
        when(calendarRepository.findByIdAndItemActive(dto.getId(), true)).thenReturn(Optional.of(olditem));

        //overlapChecks()
        when(calendarRepository.startDateBetweenThisItem(dto.getEmployeeAnumber(), true, dto.getStartDate(), dto.getEndDate() , dto.getItemType().getCompatibles()))
                .thenReturn(false);
        when(calendarRepository.endDateBetweenThisItem(dto.getEmployeeAnumber(), true, dto.getStartDate(), dto.getEndDate(), dto.getItemType().getCompatibles()))
                .thenReturn(false);
        when(calendarRepository.startDateBeforeAndEndDateAfterThisItem(dto.getEmployeeAnumber(), true, dto.getStartDate(), dto.getEndDate(), dto.getItemType().getCompatibles()))
                .thenReturn(false);

        when(calendarRepository.save(newitem)).thenReturn(newitem);

        //when
        CalendarItem itemActulizado = calendarService.updateCalendar(dto,true);

        //then
        assertNotNull(itemActulizado);
        assertEquals(newitem, itemActulizado);
    }

    @Test
    void test_invalidDate_onUpdateCalendar() {
        CalendarItemDTO dto = new CalendarItemDTO(1L, ItemType.TELEWORK, "", endDate, startDate, "A123458", "");
        CalendarItem olditem = new CalendarItem();
        //given
        //when
        //then
        assertThrows(InvalidDateRangeException.class, () -> calendarService.updateCalendar(dto,true));
    }

    @Test
    @DisplayName("update calendar item no exists")
    void test_itemNoExists_onUpdateCalendar() {
        //given
        when(calendarRepository.findByIdAndItemActive(dto.getId(), true)).thenReturn(Optional.empty());
        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> calendarService.updateCalendar(dto,true));
    }

    @ParameterizedTest
    @MethodSource(value = "getDatesConflicts")
    void test_dateConflicts_onUpdateItem(List<Instant> dates) {
        //fechas conflicto
        final Instant inicioVacciones = Instant.parse("2024-08-01T00:00:00Z");
        final Instant finVacciones = Instant.parse("2024-08-07T00:00:00Z");;

        CalendarItemDTO dto = new CalendarItemDTO(1L, ItemType.HOLIDAY, "", inicioVacciones, finVacciones, "A123458", "");
        CalendarItem olditem = new CalendarItem();

        Instant fInicio = dates.get(0);
        Instant fFin = dates.get(1);

        //given
        when(calendarRepository.findByIdAndItemActive(dto.getId(), true)).thenReturn(Optional.of(olditem));

        //comprobaciones del solape
        when(calendarRepository.startDateBetweenThisItem(dto.getEmployeeAnumber(), true, dto.getStartDate(), dto.getEndDate(), dto.getItemType().getCompatibles()))
                .thenReturn(!inicioVacciones.isBefore(fInicio) && !inicioVacciones.isAfter(fFin)); //si el incioVacaciones es mayorigual a la fecha de inicioitem y la fecha de finVacaciones es menor o igual a la de finitem

        when(calendarRepository.endDateBetweenThisItem(dto.getEmployeeAnumber(), true, dto.getStartDate(), dto.getEndDate(), dto.getItemType().getCompatibles()))
                .thenReturn(!finVacciones.isBefore(fInicio) && !finVacciones.isAfter(fFin));

        when(calendarRepository.startDateBeforeAndEndDateAfterThisItem(dto.getEmployeeAnumber(), true, dto.getStartDate(), dto.getEndDate(), dto.getItemType().getCompatibles()))
                .thenReturn(inicioVacciones.isBefore(fInicio) && finVacciones.isAfter(fFin));//he añadido una tercera condiciçon para el último caso,

        //when
        //then
        assertThrows(ExistItemException.class, () -> calendarService.updateCalendar(dto,true));
    }

    /*
     * Para rango existente: del 1/8 al 7/8:
     * - Nuevo rango del 31/7 al 1/8
     * - Nuevo rango del 7/8 al 8/8
     * - Nuevo rango del 2/8 al 3/8
     * - Nuevo rango del 31/7 al 2/8
     * - Nuevo rango del 31/7 al 10/8
     *//*
    private static Stream<List<Instant>> getDatesConflicts() {
        return Stream.of(
                Arrays.asList(Instant.parse("2024-07-31T00:00:00Z"), Instant.parse("2024-08-01T00:00:00Z")),
                Arrays.asList(Instant.parse("2024-08-07T00:00:00Z"), Instant.parse("2024-08-08T00:00:00Z")),
                Arrays.asList(Instant.parse("2024-08-02T00:00:00Z"), Instant.parse("2024-08-03T00:00:00Z")),
                Arrays.asList(Instant.parse("2024-07-31T00:00:00Z"), Instant.parse("2024-08-02T00:00:00Z")),
                Arrays.asList(Instant.parse("2024-07-31T00:00:00Z"), Instant.parse("2024-08-10T00:00:00Z"))

        );
    }

    @Test
    @DisplayName("delete calendar correct")
    void test_correct_onDeleteCalendar() {
        CalendarItem item = new CalendarItem(null, ItemType.TELEWORK, "", startDate, endDate, true, null);

        //given
        when(calendarRepository.findByIdAndItemActive(1L, true)).thenReturn(Optional.of(item));
        when(calendarRepository.save(item)).thenReturn(item);

        //when
        CalendarItem saveitem = calendarRepository.save(item);
        calendarService.deleteCalendar(1L);
        //then
        assertEquals(saveitem, item);
    }


    @Test
    @DisplayName("delete calendar no exists")
    void test_itemNoExists_onDeleteCalendar() {
        CalendarItem item = new CalendarItem();

        //given
        when(calendarRepository.findByIdAndItemActive(1L, true)).thenReturn(Optional.empty());
        //when

        //then
        assertThrows(EntityNotFoundException.class, () -> calendarService.deleteCalendar(1L));

    }

    @Test
    void test_correct_onCreate() {
        CalendarItemDTO dto = new CalendarItemDTO(null, ItemType.ABSENCE, "", startDate, endDate, "A123456", "");
        Employee emp1 = new Employee("A123458", "Cristian Mena Acedo", "Big Data", "Malaga", 0, null);
        CalendarItem item = new CalendarItem(null, ItemType.ABSENCE, "", startDate, endDate, true, emp1);

        //give
        when(calendarRepository.save(item)).thenReturn(item);
        //when

        //then
        assertEquals(item, calendarService.create(dto, emp1));
    }

    @Test
    void test_noExistEmployee_onCreate(){
        Employee emp1 = new Employee("A123458", "Cristian Mena Acedo", "Big Data", "Malaga", 0, null);
        //give
        when(calendarRepository.existsByStartDateAndEndDateAndEmployee_AnumberAndItemActiveAndItemType(dto.getStartDate(), dto.getEndDate(), dto.getEmployeeAnumber(), true, dto.getItemType()))
                .thenReturn(true);

        //when

        //then
        assertThrows(ExistItemException.class, () -> calendarService.create(dto, emp1));
    }

    @Test
    public void test_itemAusencia_incompatible_onCreateItem() {
        CalendarItemDTO dto = new CalendarItemDTO(1L, ItemType.ABSENCE, "", startDate, endDate, "A123458", "");
        Employee emp = new Employee();

        Arrays.stream(ItemType.values()).filter(item -> !ItemType.ABSENCE.getCompatibles().contains(item)) //me debería de devolver los items compatibles
                .forEach(item -> {
                    try{
                        Mockito.when(calendarRepository.existsByStartDateAndEndDateAndEmployee_AnumberAndItemActiveAndItemType(dto.getStartDate(), dto.getEndDate(), dto.getEmployeeAnumber(), true, dto.getItemType()))
                                .thenReturn(false);

                        Mockito.when(calendarRepository.startDateBetweenThisItem(dto.getEmployeeAnumber(), true, dto.getStartDate(), dto.getEndDate(), dto.getItemType().getCompatibles()))
                                .thenReturn(!dto.getItemType().getCompatibles().contains(item)); //si contiene los incompatibles = true

                        //when
                        calendarService.create(dto, emp);
                        //then
                        fail("Tipo incompatible: " + item);

                    } catch (Exception e){
                        //Si la lanza excepción = OK
                    }

                });

    }

    @Test
    public void test_itemAusencia_compatible_onCreateItem() {
        CalendarItemDTO dto = new CalendarItemDTO(1L, ItemType.ABSENCE, "", startDate, endDate, "A123458", "");
        Employee emp = new Employee();

        Arrays.stream(ItemType.values()).filter(item -> ItemType.ABSENCE.getCompatibles().contains(item)) // Filtra los items compatibles
                .forEach(item -> {
                    try {
                        Mockito.when(calendarRepository.existsByStartDateAndEndDateAndEmployee_AnumberAndItemActiveAndItemType(dto.getStartDate(), dto.getEndDate(), dto.getEmployeeAnumber(), true, dto.getItemType()))
                                .thenReturn(false);

                        Mockito.when(calendarRepository.startDateBetweenThisItem(dto.getEmployeeAnumber(), true, dto.getStartDate(), dto.getEndDate(), dto.getItemType().getCompatibles()))
                                .thenReturn(!dto.getItemType().getCompatibles().contains(item)); // si no contiene los incompatibles = false

                        // when
                        calendarService.create(dto, emp);

                        // No debería lanzarse ninguna excepción para items compatibles
                    } catch (Exception e) {
                        fail("Tipo compatible no debería lanzar excepción: " + item);
                    }

                });
    }

    @Test
    public void test_itemOficinaTeletrabajo_incompatible_onCreateItem() {
        CalendarItemDTO dto = new CalendarItemDTO(1L, ItemType.TELEWORK, "", startDate, endDate, "A123458", "");
        Employee emp = new Employee();

        Arrays.stream(ItemType.values()).filter(item -> !ItemType.TELEWORK.getCompatibles().contains(item)) //me debería de devolver los items compatibles
                .forEach(item -> {
                    try{
                        Mockito.when(calendarRepository.existsByStartDateAndEndDateAndEmployee_AnumberAndItemActiveAndItemType(dto.getStartDate(), dto.getEndDate(), dto.getEmployeeAnumber(), true, dto.getItemType()))
                                .thenReturn(false);

                        Mockito.when(calendarRepository.startDateBetweenThisItem(dto.getEmployeeAnumber(), true, dto.getStartDate(), dto.getEndDate(), dto.getItemType().getCompatibles()))
                                .thenReturn(!dto.getItemType().getCompatibles().contains(item)); //si contiene los incompatibles = true

                        //when
                        calendarService.create(dto, emp);
                        //then
                        fail("Tipo incompatible: " + item);

                    } catch (Exception e){
                        //Si la lanza excepción = OK
                    }

                });

    }

    @Test
    public void test_itemOficinaTeletrabajo_compatible_onCreateItem() {
        CalendarItemDTO dto = new CalendarItemDTO(1L, ItemType.TELEWORK, "", startDate, endDate, "A123458", "");
        Employee emp = new Employee();

        Arrays.stream(ItemType.values()).filter(item -> ItemType.TELEWORK.getCompatibles().contains(item)) // Filtra los items compatibles
                .forEach(item -> {
                    try {
                        Mockito.when(calendarRepository.existsByStartDateAndEndDateAndEmployee_AnumberAndItemActiveAndItemType(dto.getStartDate(), dto.getEndDate(), dto.getEmployeeAnumber(), true, dto.getItemType()))
                                .thenReturn(false);

                        Mockito.when(calendarRepository.startDateBetweenThisItem(dto.getEmployeeAnumber(), true, dto.getStartDate(), dto.getEndDate(), dto.getItemType().getCompatibles()))
                                .thenReturn(!dto.getItemType().getCompatibles().contains(item)); // si item pasado es diferente a los compatibles = true, en este caso false

                        // when
                        calendarService.create(dto, emp);

                        // No debería lanzarse ninguna excepción para items compatibles
                    } catch (Exception e) {
                        fail("Tipo compatible no debería lanzar excepción: " + item);
                    }

                });
    }

    @Test
    public void test_itemVacaionesBaja_incompatible_onCreateItem() {
        CalendarItemDTO dto = new CalendarItemDTO(1L, ItemType.HOLIDAY, "", startDate, endDate, "A123458", "");
        Employee emp = new Employee();

        Arrays.stream(ItemType.values())
                .forEach(item -> {
                    try{
                        Mockito.when(calendarRepository.existsByStartDateAndEndDateAndEmployee_AnumberAndItemActiveAndItemType(dto.getStartDate(), dto.getEndDate(), dto.getEmployeeAnumber(), true, dto.getItemType()))
                                .thenReturn(false);

                        Mockito.when(calendarRepository.startDateBetweenThisItem(dto.getEmployeeAnumber(), true, dto.getStartDate(), dto.getEndDate(), dto.getItemType().getCompatibles()))
                                .thenReturn(!dto.getItemType().getCompatibles().contains(item)); // si el item no está contenido en la lista = true

                        // when
                        calendarService.create(dto, emp);
                        fail("Tipo compatible: " + item);
                    }catch (Exception e){

                    }
                });
    }

    @ParameterizedTest
    @MethodSource(value = "getItemTypes")
    void test_item_Oficina_Teletrbajo_Conflicts_onCreateItem(ItemType type) { //mismo código para las 2
        CalendarItem itemVacio = new CalendarItem();

        //give
        when(calendarRepository.existsByStartDateAndEndDateAndEmployee_AnumberAndItemActiveAndItemType(dto.getStartDate(), dto.getEndDate(), dto.getEmployeeAnumber(), true, dto.getItemType())).thenReturn(false);

        when(calendarRepository.startDateBetweenThisItem(dto.getEmployeeAnumber(), true, dto.getStartDate(), dto.getEndDate(), dto.getItemType().getCompatibles()))
                .thenReturn(!type.equals(ItemType.ABSENCE));

        if(type.equals(ItemType.ABSENCE))
            when(calendarService.create(dto,new Employee())).thenReturn(itemVacio);

        //when

        //then
        if(type.equals(ItemType.ABSENCE)) {
            assertEquals(itemVacio, calendarService.create(dto, new Employee()));
        }else {
            assertThrows(ExistItemException.class, () -> calendarService.create(dto, new Employee()));
        }

    }

    @ParameterizedTest
    @MethodSource(value = "getItemTypes")
    void test_item_Baja_Vacaciones_Conflicts_onCreateItem(ItemType type) { //mismo código para las 2
        CalendarItem itemVacio = new CalendarItem();

        //give
        when(calendarRepository.existsByStartDateAndEndDateAndEmployee_AnumberAndItemActiveAndItemType(dto.getStartDate(), dto.getEndDate(), dto.getEmployeeAnumber(), true, dto.getItemType())).thenReturn(false);

        when(calendarRepository.startDateBetweenThisItem(dto.getEmployeeAnumber(), true, dto.getStartDate(), dto.getEndDate(), dto.getItemType().getCompatibles()))
                .thenReturn(true);

        //when

        //then

        assertThrows(ExistItemException.class, () -> calendarService.create(dto, new Employee()));


    }

    private static Stream<ItemType> getItemTypes(){
        return Stream.of(
                ItemType.ABSENCE,
                ItemType.SICKLEAVE,
                ItemType.TELEWORK,
                ItemType.HOLIDAY
        );
    }
}
*/