package com.availability_manager.service;

import com.availability_manager.model.DTO.EmployeeDTO;
import com.availability_manager.model.DTO.EmployeeWithRoleDTO;
import com.availability_manager.repository.EmployeeRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.availability_manager.model.Employee;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;

    @Override
    public List<Employee> getAll(){
        return repository.getAllEmployees();
    }

    @Override
    public List<Employee> getAllByLocation(@NotBlank String location){
        return repository.getEmployeeByLocation(location);
    }

    @Override
    public Optional<Employee> getEmployee(@NotBlank String anumber) {
        return repository.findById(anumber);
    }

    /**
     * Obtengo todos los empleados y los Mapeo a DTO
     * @return Lista EmployeeDTO
     */
    @Transactional
    @Override
    public List<EmployeeDTO> getAllEmployeeDTO(){
        List<Employee> employees = repository.getAllEmployees();
        List<EmployeeDTO> dtos = new ArrayList<>();

        for(Employee employee : employees) {
            EmployeeDTO dto = new EmployeeDTO();
            BeanUtils.copyProperties(employee, dto);
            dtos.add(dto);
        }

        return dtos;
    }

    /**
     * Añadir empleado
     * @param employeeDTO
     * @return EmployeeDTO
     */
    @Transactional
    @Override
    public EmployeeDTO addEmployee(@NotNull EmployeeDTO employeeDTO) {

        if(repository.existsById(employeeDTO.getAnumber())){
            throw new EntityExistsException("An employee with this number already exists");
        }

        //añadir los días de festivo al nuevo empleado---------------------------------------------------------------------

        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);

        repository.save(employee);
        return employeeDTO;
    }

    /**
     * Actualizar información de un empleado
     * @param employeeDTO
     * @return
     */
    @Override
    public EmployeeDTO updateEmployee(@NotNull EmployeeDTO employeeDTO) {
        Optional<Employee> employee = repository.findById(employeeDTO.getAnumber());

        if(employee.isPresent()) {
            Employee employee1 = employee.get();

            employee1.setAnumber(employeeDTO.getAnumber());
            employee1.setName(employeeDTO.getName());
            employee1.setTeam(employeeDTO.getTeam());
            employee1.setLocation(employeeDTO.getLocation());
            employee1.setHoliday(employeeDTO.getHoliday());
            //no modifico la lista de tiems

            repository.save(employee1);
        }else{
            throw new EntityNotFoundException("Employee not found");
        }

        return employeeDTO;
    }

    /**
     * Borrar empleado
     * @param anumber
     */
    @Transactional
    @Override
    public void deleteEmployee(@NotBlank String anumber) {
        repository.deleteById(anumber);
    }

}