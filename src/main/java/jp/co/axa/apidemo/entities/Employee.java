package jp.co.axa.apidemo.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * JPA entity representing Employee
 *
 */
@Getter @Setter
@Entity
@Table(name="EMPLOYEE")
public class Employee {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @Column(name="EMPLOYEE_NAME", nullable = false)
    private String name;

    @Column(name="EMPLOYEE_SALARY", nullable = false)
    private String salary;

    @Column(name="DEPARTMENT", nullable = false)
    private String department;

}
