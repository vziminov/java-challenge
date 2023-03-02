package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.dao.EmployeeDao;
import jp.co.axa.apidemo.dto.EmployeeDto;
import jp.co.axa.apidemo.exceptions.ApiErrors;
import jp.co.axa.apidemo.exceptions.BadRequestApiException;
import jp.co.axa.apidemo.exceptions.NotFoundApiException;
import jp.co.axa.apidemo.exceptions.ServerErrorApiException;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.PersistenceException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {EmployeeServiceImpl.class})
public class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @MockBean
    private EmployeeDao employeeDao;

    @MockBean
    private Encrypter encrypter;

    @Test
    public void testRetrieveEmployees() throws ServerErrorApiException {

        Mockito.when(employeeDao.retrieveEmployees()).thenReturn(Arrays.asList(createDbEmployee1(), createDbEmployee2()));
        Mockito.when(encrypter.decrypt(Mockito.anyString())).thenAnswer(invocation ->
                StringUtils.substringAfter((String) invocation.getArguments()[0], "encrypted:"));

        List<EmployeeDto> result = employeeService.retrieveEmployees();

        Assertions.assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result.get(0)).usingRecursiveComparison().ignoringFieldsMatchingRegexes("salary")
                .isEqualTo(createDbEmployee1());
        Assertions.assertThat(String.valueOf(result.get(0).getSalary()))
                .isEqualTo(StringUtils.substringAfter(createDbEmployee1().getSalaryEncrypted(), "encrypted:"));
        Assertions.assertThat(result.get(1)).usingRecursiveComparison().ignoringFieldsMatchingRegexes("salary")
                .isEqualTo(createDbEmployee2());
        Assertions.assertThat(String.valueOf(result.get(1).getSalary()))
                .isEqualTo(StringUtils.substringAfter(createDbEmployee2().getSalaryEncrypted(), "encrypted:"));
    }

    @Test
    public void testGetEmployeesException() {

        Mockito.when(employeeDao.retrieveEmployees()).thenThrow(new PersistenceException());

        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> employeeService.retrieveEmployees());
    }

    @Test
    public void testGetEmployee() throws BadRequestApiException, ServerErrorApiException, NotFoundApiException {

        EmployeeDto employee = createDbEmployee1();
        long id = employee.getId();
        Mockito.when(employeeDao.getEmployee(id)).thenReturn(Optional.of(employee));
        Mockito.when(encrypter.decrypt(Mockito.anyString())).thenAnswer(invocation ->
                StringUtils.substringAfter((String) invocation.getArguments()[0], "encrypted:"));

        EmployeeDto result = employeeService.getEmployee(id);

        Assertions.assertThat(result).usingRecursiveComparison().ignoringFieldsMatchingRegexes("salary")
                .isEqualTo(createDbEmployee1());
        Assertions.assertThat(String.valueOf(result.getSalary()))
                .isEqualTo(StringUtils.substringAfter(createDbEmployee1().getSalaryEncrypted(), "encrypted:"));
    }

    @Test
    public void testGetEmployeeNullId() {

        Assertions.assertThatExceptionOfType(BadRequestApiException.class)
                .isThrownBy(() -> employeeService.getEmployee(null))
                .withMessage(ApiErrors.EMPLOYEE_ID_NEGATIVE.getMessage());
    }

    @Test
    public void testGetEmployeeNegativeId() {

        Assertions.assertThatExceptionOfType(BadRequestApiException.class)
                .isThrownBy(() -> employeeService.getEmployee(-10L))
                .withMessage(ApiErrors.EMPLOYEE_ID_NEGATIVE.getMessage());
    }

    @Test
    public void testGetEmployeeNotFound() {

        Mockito.when(employeeDao.getEmployee(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(NotFoundApiException.class)
                .isThrownBy(() -> employeeService.getEmployee(1L))
                .withMessage(ApiErrors.GET_EMPLOYEE_NOT_FOUND.getMessage());
    }

    @Test
    public void testGetEmployeeException() {

        Mockito.when(employeeDao.getEmployee(Mockito.anyLong())).thenThrow(new PersistenceException());

        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> employeeService.getEmployee(1L));
    }

    @Test
    public void testSaveEmployee() throws BadRequestApiException, ServerErrorApiException {

        long newId = 555L;
        ArgumentCaptor<EmployeeDto> employeeCaptor = ArgumentCaptor.forClass(EmployeeDto.class);
        Mockito.when(employeeDao.saveEmployee(employeeCaptor.capture())).thenReturn(newId);
        Mockito.when(encrypter.encrypt(Mockito.anyString())).thenAnswer(invocation ->
                "encrypted:" + invocation.getArguments()[0]);

        Long id = employeeService.saveEmployee(createSaveEmployee());

        Assertions.assertThat(id).isEqualTo(newId);
        Assertions.assertThat(employeeCaptor.getValue()).usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes("salaryEncrypted").isEqualTo(createSaveEmployee());
        Assertions.assertThat(String.valueOf(employeeCaptor.getValue().getSalaryEncrypted()))
                .isEqualTo("encrypted:" + createSaveEmployee().getSalary());
    }

    @Test
    public void testSaveEmployeeNullName() {

        EmployeeDto employee = createSaveEmployee();
        employee.setName(null);
        Assertions.assertThatExceptionOfType(BadRequestApiException.class)
                .isThrownBy(() -> employeeService.saveEmployee(employee))
                .withMessage(ApiErrors.EMPLOYEE_NAME_EMPTY.getMessage());
    }

    @Test
    public void testSaveEmployeeNullDept() {

        EmployeeDto employee = createSaveEmployee();
        employee.setDepartment(null);
        Assertions.assertThatExceptionOfType(BadRequestApiException.class)
                .isThrownBy(() -> employeeService.saveEmployee(employee))
                .withMessage(ApiErrors.EMPLOYEE_DEPT_EMPTY.getMessage());
    }

    @Test
    public void testSaveEmployeeNullSalary() {

        EmployeeDto employee = createSaveEmployee();
        employee.setSalary(null);
        Assertions.assertThatExceptionOfType(BadRequestApiException.class)
                .isThrownBy(() -> employeeService.saveEmployee(employee))
                .withMessage(ApiErrors.EMPLOYEE_SALARY_NEGATIVE.getMessage());
    }

    @Test
    public void testSaveEmployeeNegativeSalary() {

        EmployeeDto employee = createSaveEmployee();
        employee.setSalary(-222222);
        Assertions.assertThatExceptionOfType(BadRequestApiException.class)
                .isThrownBy(() -> employeeService.saveEmployee(employee))
                .withMessage(ApiErrors.EMPLOYEE_SALARY_NEGATIVE.getMessage());
    }

    @Test
    public void testSaveEmployeeException() {

        Mockito.when(employeeDao.saveEmployee(Mockito.any())).thenThrow(new PersistenceException());

        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> employeeService.saveEmployee(createSaveEmployee()));
    }

    @Test
    public void testDeleteEmployee() throws BadRequestApiException {

        employeeService.deleteEmployee(1L);

        Mockito.verify(employeeDao, Mockito.times(1)).deleteEmployee(1L);
    }

    @Test
    public void testDeleteEmployeeNullId() {

        Assertions.assertThatExceptionOfType(BadRequestApiException.class)
                .isThrownBy(() -> employeeService.deleteEmployee(null))
                .withMessage(ApiErrors.EMPLOYEE_ID_NEGATIVE.getMessage());
    }

    @Test
    public void testDeleteEmployeeNegativeId() {

        Assertions.assertThatExceptionOfType(BadRequestApiException.class)
                .isThrownBy(() -> employeeService.deleteEmployee(-10L))
                .withMessage(ApiErrors.EMPLOYEE_ID_NEGATIVE.getMessage());
    }

    @Test
    public void testDeleteEmployeeException() {

        Mockito.doThrow(new PersistenceException()).when(employeeDao).deleteEmployee(1L);

        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> employeeService.deleteEmployee(1L));
    }


    @Test
    public void testUpdateEmployee() throws NotFoundApiException, BadRequestApiException, ServerErrorApiException {

        Mockito.when(encrypter.encrypt(Mockito.anyString())).thenAnswer(invocation ->
                "encrypted:" + invocation.getArguments()[0]);

        employeeService.updateEmployee(createSaveEmployee());

        ArgumentCaptor<EmployeeDto> employeeCaptor = ArgumentCaptor.forClass(EmployeeDto.class);
        Mockito.verify(employeeDao, Mockito.times(1)).updateEmployee(employeeCaptor.capture());
        Assertions.assertThat(employeeCaptor.getValue()).usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes("salaryEncrypted").isEqualTo(createSaveEmployee());
        Assertions.assertThat(String.valueOf(employeeCaptor.getValue().getSalaryEncrypted()))
                .isEqualTo("encrypted:" + createSaveEmployee().getSalary());
    }

    @Test
    public void testUpdateEmployeeNullName() {

        EmployeeDto employee = createSaveEmployee();
        employee.setName(null);
        Assertions.assertThatExceptionOfType(BadRequestApiException.class)
                .isThrownBy(() -> employeeService.updateEmployee(employee))
                .withMessage(ApiErrors.EMPLOYEE_NAME_EMPTY.getMessage());
    }

    @Test
    public void testUpdateEmployeeNullDept() {

        EmployeeDto employee = createSaveEmployee();
        employee.setDepartment(null);
        Assertions.assertThatExceptionOfType(BadRequestApiException.class)
                .isThrownBy(() -> employeeService.updateEmployee(employee))
                .withMessage(ApiErrors.EMPLOYEE_DEPT_EMPTY.getMessage());
    }

    @Test
    public void testUpdateEmployeeNullSalary() {

        EmployeeDto employee = createSaveEmployee();
        employee.setSalary(null);
        Assertions.assertThatExceptionOfType(BadRequestApiException.class)
                .isThrownBy(() -> employeeService.updateEmployee(employee))
                .withMessage(ApiErrors.EMPLOYEE_SALARY_NEGATIVE.getMessage());
    }

    @Test
    public void testUpdateEmployeeNegativeSalary() {

        EmployeeDto employee = createSaveEmployee();
        employee.setSalary(-222222);
        Assertions.assertThatExceptionOfType(BadRequestApiException.class)
                .isThrownBy(() -> employeeService.updateEmployee(employee))
                .withMessage(ApiErrors.EMPLOYEE_SALARY_NEGATIVE.getMessage());
    }

    @Test
    public void testUpdateEmployeeNullId() {

        EmployeeDto employee = createSaveEmployee();
        employee.setId(null);
        Assertions.assertThatExceptionOfType(BadRequestApiException.class)
                .isThrownBy(() -> employeeService.updateEmployee(employee))
                .withMessage(ApiErrors.EMPLOYEE_ID_NEGATIVE.getMessage());
    }

    @Test
    public void testUpdateEmployeeNegativeId() {

        EmployeeDto employee = createSaveEmployee();
        employee.setId(-222L);
        Assertions.assertThatExceptionOfType(BadRequestApiException.class)
                .isThrownBy(() -> employeeService.updateEmployee(employee))
                .withMessage(ApiErrors.EMPLOYEE_ID_NEGATIVE.getMessage());
    }

    @Test
    public void testUpdateEmployeeNotFound() throws NotFoundApiException {

        Mockito.doThrow(new NotFoundApiException(ApiErrors.UPDATE_EMPLOYEE_NOT_FOUND))
                .when(employeeDao).updateEmployee(Mockito.any());

        Assertions.assertThatExceptionOfType(NotFoundApiException.class)
                .isThrownBy(() -> employeeService.updateEmployee(createSaveEmployee()))
                .withMessage(ApiErrors.UPDATE_EMPLOYEE_NOT_FOUND.getMessage());
    }

    @Test
    public void testUpdateEmployeeException() throws NotFoundApiException {

        Mockito.doThrow(new PersistenceException())
                .when(employeeDao).updateEmployee(Mockito.any());

        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> employeeDao.updateEmployee(createSaveEmployee()));
    }

    private EmployeeDto createDbEmployee1() {

        EmployeeDto employee = new EmployeeDto();
        employee.setId(11L);
        employee.setName("Johnny English");
        employee.setSalaryEncrypted("encrypted:7007007");
        employee.setDepartment("Spy department");
        return employee;
    }

    private EmployeeDto createDbEmployee2() {

        EmployeeDto employee = new EmployeeDto();
        employee.setId(22L);
        employee.setName("Jon Snow");
        employee.setSalaryEncrypted("encrypted:777777");
        employee.setDepartment("Knows nothing department");
        return employee;
    }

    private EmployeeDto createSaveEmployee() {

        EmployeeDto employee = new EmployeeDto();
        employee.setId(33L);
        employee.setName("John Wick");
        employee.setSalary(11111);
        employee.setDepartment("Hitman department");
        return employee;
    }
}
