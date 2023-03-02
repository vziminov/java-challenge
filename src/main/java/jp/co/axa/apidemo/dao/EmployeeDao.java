package jp.co.axa.apidemo.dao;

import jp.co.axa.apidemo.dto.EmployeeAddDto;
import jp.co.axa.apidemo.dto.EmployeeDto;
import jp.co.axa.apidemo.exceptions.NotFoundApiException;

import java.util.List;
import java.util.Optional;

/**
 * Employee related Data Access Object
 *
 */
public interface EmployeeDao {

    /**
     * Retrieves all Employees information
     *
     * @return list of employees DTO
     */
    List<EmployeeDto> retrieveEmployees();

    /**
     * Retrieves specific Employee by id
     *
     * @param employeeId requested employee id
     * @return employee DTO optional
     */
    Optional<EmployeeDto> getEmployee(Long employeeId);

    /**
     * Saves provided information as a new Employee
     *
     * @param employee employee information
     * @return id of the saved employee
     */
    Long saveEmployee(EmployeeAddDto employee);

    /**
     * Deletes employee by the provided id.<br>
     * If the Employee is not found it is silently ignored.
     *
     * @param employeeId id of the employee to delete
     */
    void deleteEmployee(Long employeeId);

    /**
     * Updates employee based on the provided information
     *
     * @param employee employee DTO
     * @throws NotFoundApiException if employee with this id not found
     */
    void updateEmployee(EmployeeDto employee) throws NotFoundApiException;
}
