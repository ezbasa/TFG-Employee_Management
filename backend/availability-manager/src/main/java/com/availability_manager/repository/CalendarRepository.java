package com.availability_manager.repository;

import com.availability_manager.model.enumerate.ItemType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.availability_manager.model.CalendarItem;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarRepository extends JpaRepository<CalendarItem, Long> {

    List<CalendarItem> findByStartDateBetweenAndItemActive(Instant d1, Instant d2, boolean active);

    @Modifying
    @Transactional
    @Query("DELETE FROM CalendarItem i " +
            "WHERE i.employee.anumber = ?1" )
    void deleteAllByAnumber(String anumber);


    Optional<CalendarItem> findByIdAndItemActive(Long id, boolean active);

    //items por empleado
    @Query("SELECT i FROM CalendarItem  i " +
            "JOIN i.employee e " +
            "WHERE  e.anumber = :anumber AND i.itemActive = true AND i.itemType = :itemType " +
            "AND i.startDate BETWEEN :startDate AND :endDate")
    List<CalendarItem> findEmployeeItems(String anumber, ItemType itemType, Instant startDate, Instant endDate);

    //borrar festivos
    @Transactional
    @Modifying
    @Query("UPDATE CalendarItem i " +
            "SET i.itemActive = false " +
            "WHERE i.itemType = 'BANKDAY' " +
            "AND i.startDate = :startDate AND i.endDate = :endDate")
    void falseAllBankDays(Instant startDate, Instant endDate);

    //-----------------------------------------------COMPROBACIONES ITEMS--------------------------------------------

    /**
     * Devuelve true si una fecha de inicio ya guardada comienza entre las fechas del nuevoitem
     * @param employeeNumber
     * @param active
     * @param startDate
     * @param endDate
     * @param compatibles
     * @return
     */
    @Query("SELECT COUNT(i) > 0 FROM CalendarItem i " +
            "JOIN i.employee e " +
            "WHERE e.anumber = :employeeNumber " +
            "AND i.itemActive = :active " +
            "AND i.startDate BETWEEN :startDate AND :endDate " +
            "AND i.itemType NOT IN :compatibles"
    )
    boolean startDateBetweenThisItem(String employeeNumber, boolean active, Instant startDate, Instant endDate, List<ItemType> compatibles);

    //Devuelve true si una fecha de fin ya guardada termina entre las fechas del nuevoitem
    @Query("SELECT COUNT(i) > 0 FROM CalendarItem i " +
            "JOIN i.employee e " +
            "WHERE e.anumber = :employeeNumber " +
            "AND i.itemActive = :active " +
            "AND i.endDate BETWEEN :startDate AND :endDate " +
            "AND i.itemType NOT IN :compatibles"
    )
    boolean endDateBetweenThisItem(String employeeNumber, boolean active, Instant startDate, Instant endDate, List<ItemType> compatibles);

    //Devuelve true si existe una fecha de inicio anterior y una fecha fin posterior de un item, a las fechas del nuevoitem
    @Query("SELECT COUNT(i) > 0 FROM CalendarItem i " +
            "JOIN i.employee e " +
            "WHERE e.anumber = :employeeNumber " +
            "AND i.itemActive = :active " +
            "AND i.startDate < :startDate " +
            "AND i.endDate > :endDate " +
            "AND i.itemType NOT IN :compatibles"
    )
    boolean startDateBeforeAndEndDateAfterThisItem(String employeeNumber, boolean active, Instant startDate, Instant endDate, List<ItemType> compatibles);

    //busca un item por su Anumber, fecha de inicio-fin (activo)
    boolean existsByStartDateAndEndDateAndEmployee_AnumberAndItemActiveAndItemType(Instant startDate, Instant endDate, String employeeNumber, boolean active, ItemType itemType);


    //devuelve los items de un rango de fechas EMPLOYEEMANAGMENT
    @Query("SELECT i FROM CalendarItem i " +
            "WHERE (i.itemActive = true AND i.startDate BETWEEN ?1 AND ?2) " +//fecha de inicio dento de los rangos seleccionados
            "OR (i.itemActive = true AND i.startDate <= ?1 AND (i.endDate >= ?2 OR (i.endDate BETWEEN ?1 AND ?2)))"
            // fecha inicial anterior y fecha final posterior a las fechas seleccionadas
            // fecha inicial anterior y fecha fin dentro del ranqo seleccionado
    )
    List<CalendarItem> getItemsByRangeDate(Instant startDate, Instant endDate);
}
