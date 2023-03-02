package jp.co.axa.apidemo.dao;

import jp.co.axa.apidemo.dto.UserAddDto;
import jp.co.axa.apidemo.dto.UserDto;
import jp.co.axa.apidemo.exceptions.BadRequestApiException;

import java.util.List;
import java.util.Optional;

/**
 * User related Data Access Object
 *
 */
public interface UserDao {

    /**
     * Retrieves all Users information
     *
     * @return list of user DTO
     */
    List<UserDto> retrieveUsers();

    /**
     * Retrieves specific User by id
     *
     * @param userId requested user id
     * @return user DTO optional
     */
    Optional<UserDto> getUser(Long userId);

    /**
     * Retrieves specific User by username
     *
     * @param username requested user username
     * @return user DTO optional
     */
    Optional<UserDto> getUser(String username);

    /**
     * Saves provided information as a new User
     *
     * @param user user information
     * @return id of the saved user
     */
    Long saveUser(UserAddDto user) throws BadRequestApiException;

    /**
     * Deletes user by the provided id.<br>
     * If the Employee is not found it is silently ignored.
     *
     * @param userId id of the user to delete
     */
    void deleteUser(Long userId) throws BadRequestApiException;

}
