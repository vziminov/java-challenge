package jp.co.axa.apidemo.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jp.co.axa.apidemo.dto.EmployeeAddDto;
import jp.co.axa.apidemo.dto.EmployeeDto;
import jp.co.axa.apidemo.exceptions.BadRequestApiException;
import jp.co.axa.apidemo.exceptions.NotFoundApiException;
import jp.co.axa.apidemo.exceptions.ServerErrorApiException;
import jp.co.axa.apidemo.services.EmployeeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Employee")
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {

        this.employeeService = employeeService;
    }

    @ApiOperation(value = "Get Employees", notes = "Get all Employees data")
    @GetMapping("/get")
    public List<EmployeeDto> getEmployees() throws ServerErrorApiException {

        return employeeService.retrieveEmployees();
    }

    @ApiOperation(value = "Get Employee", notes = "Get Employee data with specified id")
    @GetMapping("/get/{employeeId}")
    public EmployeeDto getEmployee(@ApiParam(name =  "employeeId", value = "Employee id", example = "1", required = true)
                                   @PathVariable(name="employeeId") Long employeeId)
            throws NotFoundApiException, ServerErrorApiException, BadRequestApiException {

        return employeeService.getEmployee(employeeId);
    }

    @ApiOperation(value = "Add Employee", notes = "Adds Employee with provided information")
    @PostMapping("/add")
    public Long saveEmployee(@RequestBody EmployeeAddDto employee) throws ServerErrorApiException, BadRequestApiException {

        return employeeService.saveEmployee(employee);
    }

    @ApiOperation(value = "Delete Employee", notes = "Deletes Employee with specified id")
    @DeleteMapping("/delete/{employeeId}")
    public void deleteEmployee(@ApiParam(name =  "employeeId", value = "Employee id", example = "1", required = true)
                               @PathVariable(name="employeeId") Long employeeId) throws BadRequestApiException {

        employeeService.deleteEmployee(employeeId);
    }

    @ApiOperation(value = "Update Employee", notes = "Updates Employee with provided information")
    @PutMapping("/update")
    public void updateEmployee(@RequestBody EmployeeDto employee) throws NotFoundApiException, ServerErrorApiException,
            BadRequestApiException {

        employeeService.updateEmployee(employee);
    }

}
