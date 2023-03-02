package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.dto.UserAddDto;
import jp.co.axa.apidemo.dto.UserDto;
import jp.co.axa.apidemo.exceptions.BadRequestApiException;
import jp.co.axa.apidemo.exceptions.NotFoundApiException;

import java.util.List;

/**
 * A Service related to operations with User
 *
 */
public interface UserService {

    /**
     * Retrieves all User information
     *
     * @return list of user DTO
     */
    List<UserDto> retrieveUsers();

    /**
     * Retrieves specific User by id
     *
     * @param userId requested user id
     * @return user DTO
     * @throws NotFoundApiException if user with this id not found
     * @throws BadRequestApiException if input is incorrect
     */
    UserDto getUser(Long userId) throws NotFoundApiException, BadRequestApiException;

    /**
     * Saves provided information as a new User
     *
     * @param user user information
     * @return id of the saved user
     * @throws BadRequestApiException if input is incorrect
     */
    Long saveUser(UserAddDto user) throws BadRequestApiException;

    /**
     * Deletes user by the provided id
     *
     * @param userId id of the user to delete
     * @throws BadRequestApiException if input is incorrect
     */
    void deleteUser(Long userId) throws BadRequestApiException;

}