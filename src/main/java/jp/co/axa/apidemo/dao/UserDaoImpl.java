package jp.co.axa.apidemo.dao;

import jp.co.axa.apidemo.dto.UserAddDto;
import jp.co.axa.apidemo.dto.UserBaseDto;
import jp.co.axa.apidemo.dto.UserDto;
import jp.co.axa.apidemo.entities.User;
import jp.co.axa.apidemo.exceptions.ApiErrors;
import jp.co.axa.apidemo.exceptions.BadRequestApiException;
import jp.co.axa.apidemo.repositories.UserRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * User related Data Access Object implementation
 *
 */
@Service
public class UserDaoImpl implements UserDao {

    private final UserRepository userRepository;

    /**
     * Simple Spring injected constructor
     *
     * @param userRepository injected UserRepository bean
     */
    public UserDaoImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * {@link UserDao#retrieveUsers()}
     */
    @Override
    public List<UserDto> retrieveUsers() {

        return userRepository.findAll().stream().map(UserDto::new).collect(Collectors.toList());
    }

    /**
     * {@link UserDao#getUser(Long)}
     */
    @Override
    public Optional<UserDto> getUser(Long userId) {

        return userRepository.findById(userId).map(UserDto::new);
    }

    /**
     * {@link UserDao#getUser(String)}
     */
    @Override
    public Optional<UserDto> getUser(String username) {

        return userRepository.findByUsername(username).map(UserDto::new);
    }

    /**
     * {@link UserDao#saveUser(UserAddDto)}
     */
    @Override
    @Transactional
    public Long saveUser(UserAddDto user) throws BadRequestApiException {

        if (userRepository.existsByUsername(user.getUsername()))
            throw new BadRequestApiException(ApiErrors.USER_USERNAME_EXIST);
        return userRepository.save(user.toEntity()).getId();
    }

    /**
     * {@link UserDao#deleteUser(Long)}
     */
    @Override
    public void deleteUser(Long userId) throws BadRequestApiException {

        if (UserBaseDto.ADMIN_USERNAME.equals(userRepository.findById(userId).map(User::getUsername).orElse(null)))
            throw new BadRequestApiException(ApiErrors.USER_DELETE_ADMIN);
        try {
            // even though javadoc says that if the entity does not exist it would be silently ignored, it throws an exception
            userRepository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            // client doesn't need to know was it there before the deletion or not
        }
    }

}
