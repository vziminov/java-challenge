package jp.co.axa.apidemo.dao;

import jp.co.axa.apidemo.dto.EmployeeAddDto;
import jp.co.axa.apidemo.dto.EmployeeDto;
import jp.co.axa.apidemo.exceptions.ApiErrors;
import jp.co.axa.apidemo.exceptions.NotFoundApiException;
import jp.co.axa.apidemo.repositories.EmployeeRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Employee related Data Access Object implementation
 *
 */
@Service
public class EmployeeDaoImpl implements EmployeeDao {

    private final EmployeeRepository employeeRepository;

    /**
     * Simple Spring injected constructor
     *
     * @param employeeRepository injected EmployeeRepository bean
     */
    public EmployeeDaoImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * {@link EmployeeDao#retrieveEmployees()}
     */
    @Override
    public List<EmployeeDto> retrieveEmployees() {

        return employeeRepository.findAll().stream().map(EmployeeDto::new).collect(Collectors.toList());
    }

    /**
     * {@link EmployeeDao#getEmployee(Long)}
     */
    @Override
    public Optional<EmployeeDto> getEmployee(Long employeeId) {

        return employeeRepository.findById(employeeId).map(EmployeeDto::new);
    }

    /**
     * {@link EmployeeDao#saveEmployee(EmployeeAddDto)}
     */
    @Override
    public Long saveEmployee(EmployeeAddDto employee) {

        return employeeRepository.save(employee.toEntity()).getId();
    }

    /**
     * {@link EmployeeDao#deleteEmployee(Long)}
     */
    @Override
    public void deleteEmployee(Long employeeId) {

        try {
            // even though javadoc says that if the entity does not exist it would be silently ignored, it throws an exception
            employeeRepository.deleteById(employeeId);
        } catch (EmptyResultDataAccessException e) {
            // client doesn't need to know was it there before the deletion or not
        }
    }

    /**
     * {@link EmployeeDao#updateEmployee(EmployeeDto)}
     */
    @Override
    @Transactional
    public void updateEmployee(EmployeeDto employee) throws NotFoundApiException {

        if (!employeeRepository.existsById(employee.getId()))
            throw new NotFoundApiException(ApiErrors.UPDATE_EMPLOYEE_NOT_FOUND);

        employeeRepository.save(employee.toEntity());
    }
}
