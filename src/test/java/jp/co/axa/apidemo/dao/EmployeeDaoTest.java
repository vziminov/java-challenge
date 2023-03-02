package jp.co.axa.apidemo.dao;

import jp.co.axa.apidemo.dto.EmployeeDto;
import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.exceptions.NotFoundApiException;
import jp.co.axa.apidemo.repositories.EmployeeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.PersistenceException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {EmployeeDaoImpl.class})
public class EmployeeDaoTest {

    @Autowired
    private EmployeeDao employeeDao;

    @MockBean
    private EmployeeRepository employeeRepository;

    @Test
    public void testRetrieveEmployees() {

        Mockito.when(employeeRepository.findAll()).thenReturn(Arrays.asList(createEmployee1(), createEmployee2()));

        List<EmployeeDto> result = employeeDao.retrieveEmployees();

        Assertions.assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result.get(0)).usingRecursiveComparison().isEqualTo(new EmployeeDto(createEmployee1()));
        Assertions.assertThat(result.get(1)).usingRecursiveComparison().isEqualTo(new EmployeeDto(createEmployee2()));
    }

    @Test
    public void testGetEmployeesException() {

        Mockito.when(employeeRepository.findAll()).thenThrow(new PersistenceException());

        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> employeeDao.retrieveEmployees());
    }

    @Test
    public void testGetEmployee() {

        Employee employee = createEmployee1();
        long id = employee.getId();
        Mockito.when(employeeRepository.findById(id)).thenReturn(Optional.of(employee));

        Optional<EmployeeDto> result = employeeDao.getEmployee(id);

        Assertions.assertThat(result).get().usingRecursiveComparison().isEqualTo(new EmployeeDto(createEmployee1()));
    }

    @Test
    public void testGetEmployeeNotFound() {

        Mockito.when(employeeRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Optional<EmployeeDto> result = employeeDao.getEmployee(1L);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    public void testGetEmployeeException() {

        Mockito.when(employeeRepository.findById(Mockito.anyLong())).thenThrow(new PersistenceException());

        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> employeeDao.getEmployee(1L));
    }

    @Test
    public void testSaveEmployee() {

        long newId = 555L;
        Employee employee = createEmployee1();
        employee.setId(newId);
        Mockito.when(employeeRepository.save(Mockito.any())).thenReturn(employee);

        Long id = employeeDao.saveEmployee(new EmployeeDto(createEmployee1()));

        Assertions.assertThat(id).isEqualTo(newId);
    }

    @Test
    public void testSaveEmployeeException() {

        Mockito.when(employeeRepository.save(Mockito.any())).thenThrow(new PersistenceException());

        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> employeeDao.saveEmployee(new EmployeeDto(createEmployee1())));
    }

    @Test
    public void testDeleteEmployee() {

        employeeDao.deleteEmployee(1L);

        Mockito.verify(employeeRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteEmployeeNotFound() {

        Mockito.doThrow(new EmptyResultDataAccessException(1)).when(employeeRepository).deleteById(Mockito.anyLong());

        employeeDao.deleteEmployee(1L);

        Mockito.verify(employeeRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteEmployeeException() {

        Mockito.doThrow(new PersistenceException()).when(employeeRepository).deleteById(1L);

        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> employeeDao.deleteEmployee(1L));
    }

    @Test
    public void testUpdateEmployee() throws NotFoundApiException {

        Employee employee = createEmployee1();
        Mockito.when(employeeRepository.existsById(employee.getId())).thenReturn(true);

        employeeDao.updateEmployee(new EmployeeDto(employee));

        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);
        Mockito.verify(employeeRepository, Mockito.times(1)).save(employeeCaptor.capture());
        Assertions.assertThat(employeeCaptor.getValue()).usingRecursiveComparison().isEqualTo(createEmployee1());
    }

    @Test
    public void testUpdateEmployeeNotFound() throws NotFoundApiException {

        Mockito.when(employeeRepository.existsById(createEmployee1().getId())).thenReturn(false);

        Assertions.assertThatExceptionOfType(NotFoundApiException.class)
                .isThrownBy(() -> employeeDao.updateEmployee(new EmployeeDto(createEmployee1())));
    }

    @Test
    public void testUpdateEmployeeException() {

        Mockito.when(employeeRepository.save(Mockito.any())).thenThrow(new PersistenceException());
        Mockito.when(employeeRepository.existsById(Mockito.any())).thenReturn(true);

        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> employeeDao.updateEmployee(new EmployeeDto(createEmployee1())));
    }

    private Employee createEmployee1() {

        Employee employee = new Employee();
        employee.setId(11);
        employee.setName("Johnny English");
        employee.setSalary("encrypted:7007007");
        employee.setDepartment("Spy department");
        return employee;
    }

    private Employee createEmployee2() {

        Employee employee = new Employee();
        employee.setId(22);
        employee.setName("Jon Snow");
        employee.setSalary("encrypted:777777");
        employee.setDepartment("Knows nothing department");
        return employee;
    }
}
