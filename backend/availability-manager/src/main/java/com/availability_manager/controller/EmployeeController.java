/*
package com.dekra.availability_manager.controller;

import com.dekra.availability_manager.model.DTO.EmployeeDTO;
import com.dekra.availability_manager.model.Employee;
import com.dekra.availability_manager.service.EmployeeService;
import com.dekra.availability_manager.service.EmployeeServiceManagment;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Controller
@Validated
@RequestMapping ("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    EmployeeServiceManagment employeeManagment;

    @Operation(
            summary = "Obtener empleados por rango de fechas",
            description = "Este endpoint devuelve una lista de empleados dentro de un rango de fechas especificado"
    )
    @GetMapping("/range")
    public ResponseEntity<List<EmployeeDTO>> getRange(@NotNull @RequestParam Instant startDate,
                                                   @NotNull @RequestParam Instant endDate) {

        return ResponseEntity.ok(employeeManagment.getEmployeesByDatesRange(startDate, endDate));
    }

    @Operation(summary = "Obtener todos los empleados",
            description = "Este endpoint devuelve una lista de todos los empleados")
    @GetMapping()
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployeeDTO());
    }

    @Operation(summary = "Crear un nuevo empleado",
            description = "Este endpoint permite crear un nuevo empleado")
    @PostMapping()
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody @Valid @NotNull EmployeeDTO employeeDTO) {
        return ResponseEntity.ok(employeeService.addEmployee(employeeDTO));
    }

    @Operation(summary = "Actualizar un empleado existente",
            description = "Este endpoint permite actualizar los datos de un empleado existente")
    @PutMapping()
    public ResponseEntity<EmployeeDTO> updateEmployee(@RequestBody @Valid @NotNull EmployeeDTO employeeDTO) {
        return ResponseEntity.ok(employeeService.updateEmployee(employeeDTO));
    }

    @Operation(summary = "Eliminar un empleado",
            description = "Este endpoint permite eliminar un empleado por su número de identificación")
    @DeleteMapping()
    public ResponseEntity<Void> deleteEmployee(@RequestParam @NotBlank String anumber) {
        employeeManagment.deleteEmployee(anumber);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<String> handleException(EntityExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleException(EntityNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}*/

package com.dekra.availability_manager.controller;

import com.dekra.availability_manager.model.DTO.EmployeeDTO;
import com.dekra.availability_manager.model.Employee;
import com.dekra.availability_manager.service.EmployeeService;
import com.dekra.availability_manager.service.EmployeeServiceManagment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Tag(name = "Employee Management", description = "APIs for managing employees")
@Controller
@Validated
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    EmployeeServiceManagment employeeManagment;

    @Operation(
            summary = "Obtener todos los empleados",
            description = "Este endpoint devuelve una lista de todos los empleados",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de empleados obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
            }
    )
    @GetMapping()
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployeeDTO());
    }

    @Operation(
            summary = "Obtener empleados por rango de fechas",
            description = "Este endpoint devuelve una lista de empleados dentro de un rango de fechas especificado",
            parameters = {
                    @Parameter(name = "startDate", description = "Fecha de inicio del rango", required = true, schema = @Schema(type = "string", format = "date-time")),
                    @Parameter(name = "endDate", description = "Fecha de fin del rango", required = true, schema = @Schema(type = "string", format = "date-time"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de empleados obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
            }
    )
    @GetMapping("/range")
    public ResponseEntity<List<EmployeeDTO>> getRange(@NotNull @RequestParam Instant startDate,
                                                      @NotNull @RequestParam Instant endDate) {
        return ResponseEntity.ok(employeeManagment.getEmployeesByDatesRange(startDate, endDate));
    }

    @Operation(
            summary = "Crear un nuevo empleado",
            description = "Este endpoint permite crear un nuevo empleado",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo empleado",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Empleado creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
            }
    )
    @PostMapping()
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody @Valid @NotNull EmployeeDTO employeeDTO) {
        return ResponseEntity.ok(employeeService.addEmployee(employeeDTO));
    }

    @Operation(
            summary = "Actualizar un empleado existente",
            description = "Este endpoint permite actualizar los datos de un empleado existente",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del empleado a actualizar",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Empleado actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
            }
    )
    @PutMapping()
    public ResponseEntity<EmployeeDTO> updateEmployee(@RequestBody @Valid @NotNull EmployeeDTO employeeDTO) {
        return ResponseEntity.ok(employeeService.updateEmployee(employeeDTO));
    }

    @Operation(
            summary = "Eliminar un empleado",
            description = "Este endpoint permite eliminar un empleado por su número de identificación",
            parameters = {
                    @Parameter(name = "anumber", description = "Número de identificación del empleado", required = true, schema = @Schema(type = "string"))
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Empleado eliminado exitosamente", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
            }
    )
    @DeleteMapping()
    public ResponseEntity<Void> deleteEmployee(@RequestParam @NotBlank String anumber) {
        employeeManagment.deleteEmployee(anumber);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<String> handleException(EntityExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleException(EntityNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
