package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.dto.EmployeeAddDto;
import jp.co.axa.apidemo.dto.EmployeeDto;
import jp.co.axa.apidemo.exceptions.BadRequestApiException;
import jp.co.axa.apidemo.exceptions.NotFoundApiException;
import jp.co.axa.apidemo.exceptions.ServerErrorApiException;

import java.util.List;

/**
 * A Service related to operations with Employee
 *
 */
public interface EmployeeService {

    /**
     * Retrieves all Employees information
     *
     * @return list of employees DTO
     * @throws ServerErrorApiException if encryption fails
     */
    List<EmployeeDto> retrieveEmployees() throws ServerErrorApiException;

    /**
     * Retrieves specific Employee by id
     *
     * @param employeeId requested employee id
     * @return employee DTO
     * @throws NotFoundApiException if employee with this id not found
     * @throws ServerErrorApiException if encryption fails
     * @throws BadRequestApiException if input is incorrect
     */
    EmployeeDto getEmployee(Long employeeId) throws NotFoundApiException, ServerErrorApiException, BadRequestApiException;

    /**
     * Saves provided information as a new Employee
     *
     * @param employee employee information
     * @return id of the saved employee
     * @throws ServerErrorApiException if encryption fails
     * @throws BadRequestApiException if input is incorrect
     */
    Long saveEmployee(EmployeeAddDto employee) throws ServerErrorApiException, BadRequestApiException;

    /**
     * Deletes employee by the provided id
     *
     * @param employeeId id of the employee to delete
     * @throws BadRequestApiException if input is incorrect
     */
    void deleteEmployee(Long employeeId) throws BadRequestApiException;

    /**
     * Updates employee based on the provided information
     *
     * @param employee employee DTO
     * @throws NotFoundApiException if employee with this id not found
     * @throws ServerErrorApiException if encryption fails
     * @throws BadRequestApiException if input is incorrect
     */
    void updateEmployee(EmployeeDto employee) throws NotFoundApiException, ServerErrorApiException, BadRequestApiException;
}