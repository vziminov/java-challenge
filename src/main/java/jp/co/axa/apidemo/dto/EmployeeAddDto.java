package jp.co.axa.apidemo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jp.co.axa.apidemo.entities.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO with Employee information needed to add new Employee
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "EmployeeAddition", description = "Employee addition data")
public class EmployeeAddDto {

    @ApiModelProperty(value = "Employee full name", required = true, example = "Johnny English")
    private String name;

    @ApiModelProperty(value = "Employee salary in yen", required = true, example = "7007007")
    private Integer salary;

    @ApiModelProperty(value = "Employee department", required = true, example = "Spy department")
    private String department;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private String salaryEncrypted;

    /**
     * Maps this DTO back to the Employee entity
     *
     * @return Employee entity
     */
    public Employee toEntity() {

        Employee employee = new Employee();
        employee.setName(name);
        employee.setSalary(salaryEncrypted);
        employee.setDepartment(department);
        return employee;
    }
}
