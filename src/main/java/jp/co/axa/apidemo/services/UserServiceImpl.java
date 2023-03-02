package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.AppProperties;
import jp.co.axa.apidemo.dao.UserDao;
import jp.co.axa.apidemo.dto.Role;
import jp.co.axa.apidemo.dto.UserAddDto;
import jp.co.axa.apidemo.dto.UserBaseDto;
import jp.co.axa.apidemo.dto.UserDto;
import jp.co.axa.apidemo.exceptions.ApiErrors;
import jp.co.axa.apidemo.exceptions.BadRequestApiException;
import jp.co.axa.apidemo.exceptions.NotFoundApiException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * A Service implementation related to operations with User
 *
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;

    /**
     * Simple Spring injected constructor
     *
     * @param userDao injected UserDao bean
     * @param passwordEncoder injected PasswordEncoder bean
     */
    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder, AppProperties appProperties) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.appProperties = appProperties;
    }

    /**
     * Creates an admin user if it doesn't exist
     *
     */
    @PostConstruct
    protected void createAdmin() throws IOException {

        try {
            UserAddDto user = new UserAddDto();
            user.setUsername(UserBaseDto.ADMIN_USERNAME);

            String passwordFile = appProperties.getAdminPasswordFile();
            // no file provided
            if (StringUtils.isEmpty(passwordFile)) {
                user.setPassword(UserBaseDto.ADMIN_PASSWORD);
            } else {
                Path secretPath = Paths.get(passwordFile);
                // when external path is provided, but incorrect need to fail-fast
                if (!Files.exists(secretPath))
                    throw new IllegalStateException("No external secret key file found in: " + passwordFile);

                user.setPassword(new String(Files.readAllBytes(secretPath), StandardCharsets.UTF_8));
            }
            user.setRole(Role.ADMINISTRATOR);
            saveUser(user);
        } catch (BadRequestApiException e) {
            // if it exists we just continue
        }
    }

    /**
     * Retrieves all User information<br>
     * Password stored only in hashed form - no sense in returning it.
     *
     * @return list of user DTO
     */
    @Override
    public List<UserDto> retrieveUsers() {

        return userDao.retrieveUsers();
    }

    /**
     * Retrieves specific User by id.<br>
     * Validates that id is a positive number.<br>
     * Password stored only in hashed form - no sense in returning it.
     *
     * @param userId requested user id
     * @return user DTO
     * @throws NotFoundApiException if user with this id not found
     * @throws BadRequestApiException if input is incorrect
     */
    @Override
    public UserDto getUser(Long userId) throws NotFoundApiException, BadRequestApiException {

        ValidationUtils.validatePositive(userId, ApiErrors.USER_ID_NEGATIVE);

        return userDao.getUser(userId)
                .orElseThrow(() -> new NotFoundApiException(ApiErrors.GET_USER_NOT_FOUND));
    }

    /**
     * Saves provided information as a new User.<br>
     * Validates that username and password are non-empty strings while role is present.<br>
     * Password is hashed for DB storage.
     *
     * @param user user information
     * @return id of the saved user
     * @throws BadRequestApiException if input is incorrect
     */
    @Override
    public Long saveUser(UserAddDto user) throws BadRequestApiException {

        ValidationUtils.validateNotEmpty(user.getUsername(), ApiErrors.USER_USERNAME_EMPTY);
        ValidationUtils.validateNotEmpty(user.getPassword(), ApiErrors.USER_PASSWORD_EMPTY);
        ValidationUtils.validateNotNull(user.getRole(), ApiErrors.USER_ROLE_EMPTY);

        user.setPasswordHash(passwordEncoder.encode(String.valueOf(user.getPassword())));
        return userDao.saveUser(user);
    }

    /**
     * Deletes user by the provided id.<br>
     * Validates that id is a positive number.
     *
     * @param userId id of the user to delete
     * @throws BadRequestApiException if input is incorrect
     */
    @Override
    public void deleteUser(Long userId) throws BadRequestApiException {

        ValidationUtils.validatePositive(userId, ApiErrors.USER_ID_NEGATIVE);

        userDao.deleteUser(userId);
    }

}