package jp.co.axa.apidemo.dao;

import jp.co.axa.apidemo.dto.Role;
import jp.co.axa.apidemo.dto.UserAddDto;
import jp.co.axa.apidemo.dto.UserBaseDto;
import jp.co.axa.apidemo.dto.UserDto;
import jp.co.axa.apidemo.entities.User;
import jp.co.axa.apidemo.exceptions.ApiErrors;
import jp.co.axa.apidemo.exceptions.BadRequestApiException;
import jp.co.axa.apidemo.repositories.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.PersistenceException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {UserDaoImpl.class})
public class UserDaoTest {

    @Autowired
    private UserDao userDao;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testRetrieveUsers() {

        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(createUser1(), createUser2()));

        List<UserDto> result = userDao.retrieveUsers();

        Assertions.assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result.get(0)).usingRecursiveComparison().isEqualTo(new UserDto(createUser1()));
        Assertions.assertThat(result.get(1)).usingRecursiveComparison().isEqualTo(new UserDto(createUser2()));
    }

    @Test
    public void testGetUsersException() {

        Mockito.when(userRepository.findAll()).thenThrow(new PersistenceException());

        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> userDao.retrieveUsers());
    }

    @Test
    public void testGetUserById() {

        User user = createUser1();
        long id = user.getId();
        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        Optional<UserDto> result = userDao.getUser(id);

        Assertions.assertThat(result).get().usingRecursiveComparison().isEqualTo(new UserDto(createUser1()));
    }

    @Test
    public void testGetUserByIdNotFound() {

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Optional<UserDto> result = userDao.getUser(1L);

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    public void testGetUserByIdException() {

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenThrow(new PersistenceException());

        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> userDao.getUser(1L));
    }

    @Test
    public void testGetUserByUsername() {

        User user = createUser1();
        String username = user.getUsername();
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Optional<UserDto> result = userDao.getUser(username);

        Assertions.assertThat(result).get().usingRecursiveComparison().isEqualTo(new UserDto(createUser1()));
    }

    @Test
    public void testGetUserByUsernameNotFound() {

        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenReturn(Optional.empty());

        Optional<UserDto> result = userDao.getUser("someone");

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    public void testGetUserByUsernameException() {

        Mockito.when(userRepository.findByUsername(Mockito.anyString())).thenThrow(new PersistenceException());

        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> userDao.getUser("someone"));
    }

    @Test
    public void testSaveUser() throws BadRequestApiException {

        long newId = 555L;
        User user = createUser1();
        user.setId(newId);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);

        Long id = userDao.saveUser(createUserAddDto());

        Assertions.assertThat(id).isEqualTo(newId);
    }

    @Test
    public void testUpdateUserExists() {

        Mockito.when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(true);

        Assertions.assertThatExceptionOfType(BadRequestApiException.class)
                .isThrownBy(() -> userDao.saveUser(createUserAddDto()))
                .withMessage(ApiErrors.USER_USERNAME_EXIST.getMessage());
    }

    @Test
    public void testSaveUserException() {

        Mockito.when(userRepository.save(Mockito.any())).thenThrow(new PersistenceException());

        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> userDao.saveUser(createUserAddDto()));
    }

    @Test
    public void testDeleteUser() throws BadRequestApiException {

        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        userDao.deleteUser(1L);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteUserNotFound() throws BadRequestApiException {

        Mockito.doThrow(new EmptyResultDataAccessException(1)).when(userRepository).deleteById(Mockito.anyLong());
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        userDao.deleteUser(1L);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteUserAdmin() {

        User user = new User();
        user.setUsername(UserBaseDto.ADMIN_USERNAME);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        Assertions.assertThatExceptionOfType(BadRequestApiException.class)
                .isThrownBy(() -> userDao.deleteUser(1L))
                .withMessage(ApiErrors.USER_DELETE_ADMIN.getMessage());
    }

    @Test
    public void testDeleteUserException() {

        Mockito.doThrow(new PersistenceException()).when(userRepository).deleteById(1L);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> userDao.deleteUser(1L));
    }

    private User createUser1() {

        User user = new User();
        user.setId(11);
        user.setUsername("johnny.english");
        user.setPasswordHash("hashed:bestjohnny");
        user.setRole(Role.ADMINISTRATOR);
        return user;
    }

    private User createUser2() {

        User user = new User();
        user.setId(22);
        user.setUsername("jon.snow");
        user.setPasswordHash("hashed:winteriscoming");
        user.setRole(Role.EDITOR);
        return user;
    }

    private UserAddDto createUserAddDto() {

        User user = createUser1();
        UserAddDto addDto = new UserAddDto();
        addDto.setUsername(user.getUsername());
        addDto.setPasswordHash(user.getPasswordHash());
        addDto.setRole(user.getRole());
        return addDto;
    }
}
