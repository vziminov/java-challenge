package jp.co.axa.apidemo.entities;

import jp.co.axa.apidemo.dto.Role;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * JPA entity representing User.
 *
 */
@Getter @Setter
@Entity
@Table(name="USERS", indexes = {
        @Index(name = "username_index", columnList = "username")
})
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @Column(name="USERNAME", nullable = false, unique = true)
    private String username;

    @Column(name="PASSWORD_HASH", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name="ROLE", nullable = false)
    private Role role;

}
