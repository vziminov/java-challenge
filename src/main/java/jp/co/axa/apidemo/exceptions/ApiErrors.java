package jp.co.axa.apidemo.exceptions;

import lombok.Getter;

/**
 * An Enum for different types of handled situations that generate Error to the API client
 *
 */
@Getter
public enum ApiErrors {

    USER_ID_NEGATIVE("User id should be a positive number"),
    USER_USERNAME_EMPTY("User username should not be empty"),
    USER_PASSWORD_EMPTY("User password should not be empty"),
    USER_ROLE_EMPTY("User role should not be empty"),
    GET_USER_NOT_FOUND("User with requested id does not exist"),
    USER_USERNAME_EXIST("User with provided username already exist"),
    USER_DELETE_ADMIN("Could not delete admin user"),
    EMPLOYEE_ID_NEGATIVE("Employee id should be a positive number"),
    EMPLOYEE_NAME_EMPTY("Employee name should not be empty"),
    EMPLOYEE_DEPT_EMPTY("Employee department should not be empty"),
    EMPLOYEE_SALARY_NEGATIVE("Employee salary should be a positive number"),
    UPDATE_EMPLOYEE_NOT_FOUND("Updated employee does not exist"),
    GET_EMPLOYEE_NOT_FOUND("Employee with requested id does not exist"),
    ENCRYPTION_FAILED("Unable to encryption data"),
    DECRYPTION_FAILED("Unable to decryption data");


    private final String message;

    ApiErrors(String message) {
        this.message = message;
    }
}
