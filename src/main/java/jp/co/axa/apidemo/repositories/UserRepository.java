package jp.co.axa.apidemo.repositories;

import jp.co.axa.apidemo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * A paging CRUD repository for User
 *
 */
@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    /**
     * Checks is user with provided username already exists
     *
     * @param username username to check
     * @return true is exists
     */
    boolean existsByUsername(String username);

    /**
     * Fetches User from the DB by the username
     *
     * @param username username to fetch
     * @return Optional of the User
     */
    Optional<User> findByUsername(String username);
}
