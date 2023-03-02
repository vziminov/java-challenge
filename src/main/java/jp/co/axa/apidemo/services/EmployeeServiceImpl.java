package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.dao.EmployeeDao;
import jp.co.axa.apidemo.dto.EmployeeAddDto;
import jp.co.axa.apidemo.dto.EmployeeDto;
import jp.co.axa.apidemo.exceptions.ApiErrors;
import jp.co.axa.apidemo.exceptions.BadRequestApiException;
import jp.co.axa.apidemo.exceptions.NotFoundApiException;
import jp.co.axa.apidemo.exceptions.ServerErrorApiException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * A Service implementation related to operations with Employee
 *
 */
@Service
public class EmployeeServiceImpl implements EmployeeService{

    private final EmployeeDao employeeDao;
    private final Encrypter encrypter;

    /**
     * Simple Spring injected constructor
     *
     * @param employeeDao injected EmployeeDao bean
     * @param encrypter injected Encrypter bean
     */
    public EmployeeServiceImpl(EmployeeDao employeeDao, Encrypter encrypter) {
        this.employeeDao = employeeDao;
        this.encrypter = encrypter;
    }

    /**
     * Retrieves all Employees information<br>
     * Decrypts salary values, that stored in DB in encrypted form.
     *
     * @return list of employees DTO
     * @throws ServerErrorApiException if encryption fails
     */
    @Override
    public List<EmployeeDto> retrieveEmployees() throws ServerErrorApiException {

        List<EmployeeDto> employees = employeeDao.retrieveEmployees();
        for (EmployeeDto employee : employees) {
            employee.setSalary(Integer.parseInt(encrypter.decrypt(employee.getSalaryEncrypted())));
        }
        return employees;
    }

    /**
     * Retrieves specific Employee by id.<br>
     * Validates that id is a positive number.<br>
     * Decrypts salary value, that stored in DB in encrypted form.
     *
     * @param employeeId requested employee id
     * @return employee DTO
     * @throws NotFoundApiException if employee with this id not found
     * @throws ServerErrorApiException if encryption fails
     * @throws BadRequestApiException if input is incorrect
     */
    @Override
    public EmployeeDto getEmployee(Long employeeId) throws NotFoundApiException, ServerErrorApiException, BadRequestApiException {

        ValidationUtils.validatePositive(employeeId, ApiErrors.EMPLOYEE_ID_NEGATIVE);

        EmployeeDto employee = employeeDao.getEmployee(employeeId)
                .orElseThrow(() -> new NotFoundApiException(ApiErrors.GET_EMPLOYEE_NOT_FOUND));
        employee.setSalary(Integer.parseInt(encrypter.decrypt(employee.getSalaryEncrypted())));
        return employee;
    }

    /**
     * Saves provided information as a new Employee.<br>
     * Validates that salary is positive number while name and department are non-empty strings.<br>
     * Salary value is encrypted for DB storage.
     *
     * @param employee employee information
     * @return id of the saved employee
     * @throws ServerErrorApiException if encryption fails
     * @throws BadRequestApiException if input is incorrect
     */
    @Override
    public Long saveEmployee(EmployeeAddDto employee) throws ServerErrorApiException, BadRequestApiException {

        ValidationUtils.validateNotEmpty(employee.getName(), ApiErrors.EMPLOYEE_NAME_EMPTY);
        ValidationUtils.validateNotEmpty(employee.getDepartment(), ApiErrors.EMPLOYEE_DEPT_EMPTY);
        ValidationUtils.validatePositive(employee.getSalary(), ApiErrors.EMPLOYEE_SALARY_NEGATIVE);

        employee.setSalaryEncrypted(encrypter.encrypt(String.valueOf(employee.getSalary())));
        return employeeDao.saveEmployee(employee);
    }

    /**
     * Deletes employee by the provided id.<br>
     * Validates that id is a positive number.
     *
     * @param employeeId id of the employee to delete
     * @throws BadRequestApiException if input is incorrect
     */
    @Override
    public void deleteEmployee(Long employeeId) throws BadRequestApiException {

        ValidationUtils.validatePositive(employeeId, ApiErrors.EMPLOYEE_ID_NEGATIVE);

        employeeDao.deleteEmployee(employeeId);
    }

    /**
     * Updates employee based on the provided information.<br>
     * Validates that id and salary are positive numbers while name and department are non-empty strings.<br>
     * Salary value is encrypted for DB storage.
     *
     * @param employee employee DTO
     * @throws NotFoundApiException if employee with this id not found
     * @throws ServerErrorApiException if encryption fails
     * @throws BadRequestApiException if input is incorrect
     */
    @Override
    public void updateEmployee(EmployeeDto employee) throws NotFoundApiException, ServerErrorApiException, BadRequestApiException {

        ValidationUtils.validatePositive(employee.getId(), ApiErrors.EMPLOYEE_ID_NEGATIVE);
        ValidationUtils.validateNotEmpty(employee.getName(), ApiErrors.EMPLOYEE_NAME_EMPTY);
        ValidationUtils.validateNotEmpty(employee.getDepartment(), ApiErrors.EMPLOYEE_DEPT_EMPTY);
        ValidationUtils.validatePositive(employee.getSalary(), ApiErrors.EMPLOYEE_SALARY_NEGATIVE);

        employee.setSalaryEncrypted(encrypter.encrypt(String.valueOf(employee.getSalary())));
        employeeDao.updateEmployee(employee);
    }
}