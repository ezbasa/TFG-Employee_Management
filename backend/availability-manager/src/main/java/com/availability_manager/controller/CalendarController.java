package com.availability_manager.controller;

import com.availability_manager.model.CalendarItem;
import com.availability_manager.model.DTO.CalendarItemDTO;
import com.availability_manager.exception.ExistItemException;
import com.availability_manager.exception.InvalidDateRangeException;
import com.availability_manager.service.CalendarService;
import com.availability_manager.service.CalendarServiceManagement;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.time.Instant;
import java.util.List;

@Tag(name = "Calendar Management", description = "APIs for managing calendar items")
@RestController
@Validated
@RequestMapping("/item-calendar")
public class CalendarController {

    @Autowired
    private CalendarService service;

    @Autowired
    private CalendarServiceManagement management;

    /*
    @GetMapping("/test")
    public String test() {
        return "Controller is working!";
    }
     */


    @Operation(
            summary = "Obtener ítems del calendario por rango de fechas",
            description = "Este endpoint devuelve una lista de ítems del calendario dentro de un rango de fechas especificado",
            parameters = {
                    @Parameter(name = "fechaInicio", description = "Fecha de inicio del rango", required = true, schema = @Schema(type = "string", format = "date-time")),
                    @Parameter(name = "fechaFin", description = "Fecha de fin del rango", required = true, schema = @Schema(type = "string", format = "date-time"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de ítems del calendario obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CalendarItem.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Ítem del calendario no encontrado", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
            }
    )
    @GetMapping()
    public ResponseEntity<List<CalendarItem>> getCalendar(@NotNull @RequestParam Instant fechaInicio,
                                                          @NotNull @RequestParam Instant fechaFin) {
        return ResponseEntity.ok(service.getCalendars(fechaInicio, fechaFin));
    }

    @Operation(
            summary = "Añadir un nuevo ítem al calendario",
            description = "Este endpoint permite añadir un nuevo ítem al calendario",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo ítem del calendario",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CalendarItemDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Ítem del calendario creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CalendarItemDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content),
                    @ApiResponse(responseCode = "409", description = "El ítem del calendario ya existe", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<List<CalendarItemDTO>> addCalendar(@Valid @NotNull @RequestBody CalendarItemDTO dto) {
        return new ResponseEntity<>(management.insertCalendar(dto), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Actualizar un ítem del calendario",
            description = "Este endpoint permite actualizar los datos de un ítem del calendario existente",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del ítem del calendario a actualizar",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CalendarItemDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ítem del calendario actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CalendarItemDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Ítem del calendario no encontrado", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
            }
    )
    @PutMapping
    public ResponseEntity<List<CalendarItemDTO>> updateCalendar(@NotBlank @Valid @RequestBody CalendarItemDTO dto) {
        return ResponseEntity.ok(management.updateCalendar(dto));
    }

    @Operation(
            summary = "Eliminar un ítem del calendario",
            description = "Este endpoint permite eliminar un ítem del calendario por su ID",
            parameters = {
                    @Parameter(name = "id", description = "ID del ítem del calendario", required = true, schema = @Schema(type = "integer")),
                    @Parameter(name = "anumber", description = "string usado en los filtros de JWT", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Ítem del calendario eliminado exitosamente", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Ítem del calendario no encontrado", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
            }
    )
    @DeleteMapping
    public ResponseEntity<Void> deleteCalendar(@NotNull @RequestParam Long id, String anumber) {
        service.deleteCalendar(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<String> handleException(InvalidDateRangeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleException(EntityNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ExistItemException.class)
    public ResponseEntity<String> handleException(ExistItemException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleException(ValidationException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }
}
