package jp.co.axa.apidemo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jp.co.axa.apidemo.entities.Employee;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * A DTO representing full Employee information including id
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ApiModel(value = "Employee", description = "Employee full data")
public class EmployeeDto extends EmployeeAddDto {

    @ApiModelProperty(value = "Employee id", required = true, example = "1")
    private Long id;

    /**
     * A constructor that creates DTO based on the Employee entity
     *
     * @param employee Employee entity
     */
    public EmployeeDto(Employee employee) {

        super(employee.getName(), null, employee.getDepartment(), employee.getSalary());
        this.id = employee.getId();
    }

    /**
     * Maps this DTO back to the Employee entity
     *
     * @return Employee entity
     */
    public Employee toEntity() {

        Employee employee = super.toEntity();
        employee.setId(id);
        return employee;
    }
}
