package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.AppProperties;
import jp.co.axa.apidemo.dao.UserDao;
import jp.co.axa.apidemo.dto.Role;
import jp.co.axa.apidemo.dto.UserAddDto;
import jp.co.axa.apidemo.dto.UserDto;
import jp.co.axa.apidemo.exceptions.ApiErrors;
import jp.co.axa.apidemo.exceptions.BadRequestApiException;
import jp.co.axa.apidemo.exceptions.NotFoundApiException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.PersistenceException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {UserServiceImpl.class})
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserDao userDao;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AppProperties appProperties;

    @Test
    public void testAdminPasswordNonexistent() {

        Mockito.when(appProperties.getAdminPasswordFile()).thenReturn("/notexistingpath/forsure");
        Assertions.assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> new UserServiceImpl(userDao, passwordEncoder, appProperties).createAdmin());
    }

    @Test
    public void testRetrieveUsers() {

        Mockito.when(userDao.retrieveUsers()).thenReturn(Arrays.asList(createDbUser1(), createDbUser2()));

        List<UserDto> result = userService.retrieveUsers();

        Assertions.assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result.get(0)).usingRecursiveComparison().isEqualTo(createDbUser1());
        Assertions.assertThat(result.get(1)).usingRecursiveComparison().isEqualTo(createDbUser2());
    }

    @Test
    public void testGetUsersException() {

        Mockito.when(userDao.retrieveUsers()).thenThrow(new PersistenceException());

        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> userService.retrieveUsers());
    }

    @Test
    public void testGetUser() throws BadRequestApiException, NotFoundApiException {

        UserDto user = createDbUser1();
        long id = user.getId();
        Mockito.when(userDao.getUser(id)).thenReturn(Optional.of(user));

        UserDto result = userService.getUser(id);

        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(createDbUser1());
    }

    @Test
    public void testGetUserNullId() {

        Assertions.assertThatExceptionOfType(BadRequestApiException.class)
                .isThrownBy(() -> userService.getUser(null))
                .withMessage(ApiErrors.USER_ID_NEGATIVE.getMessage());
    }

    @Test
    public void testGetUserNegativeId() {

        Assertions.assertThatExceptionOfType(BadRequestApiException.class)
                .isThrownBy(() -> userService.getUser(-10L))
                .withMessage(ApiErrors.USER_ID_NEGATIVE.getMessage());
    }

    @Test
    public void testGetUserNotFound() {

        Mockito.when(userDao.getUser(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(NotFoundApiException.class)
                .isThrownBy(() -> userService.getUser(1L))
                .withMessage(ApiErrors.GET_USER_NOT_FOUND.getMessage());
    }

    @Test
    public void testGetUserException() {

        Mockito.when(userDao.getUser(Mockito.anyLong())).thenThrow(new PersistenceException());

        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> userService.getUser(1L));
    }

    @Test
    public void testSaveUser() throws BadRequestApiException {

        long newId = 555L;
        ArgumentCaptor<UserAddDto> userCaptor = ArgumentCaptor.forClass(UserAddDto.class);
        Mockito.when(userDao.saveUser(userCaptor.capture())).thenReturn(newId);
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenAnswer(invocation ->
                "hashed:" + invocation.getArguments()[0]);

        Long id = userService.saveUser(createSaveUser());

        Assertions.assertThat(id).isEqualTo(newId);
        Assertions.assertThat(userCaptor.getValue()).usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes("passwordHash").isEqualTo(createSaveUser());
        Assertions.assertThat(String.valueOf(userCaptor.getValue().getPasswordHash()))
                .isEqualTo("hashed:" + createSaveUser().getPassword());
    }

    @Test
    public void testSaveUserNullUsername() {

        UserAddDto user = createSaveUser();
        user.setUsername(null);
        Assertions.assertThatExceptionOfType(BadRequestApiException.class)
                .isThrownBy(() -> userService.saveUser(user))
                .withMessage(ApiErrors.USER_USERNAME_EMPTY.getMessage());
    }

    @Test
    public void testSaveUserNullPassword() {

        UserAddDto user = createSaveUser();
        user.setPassword(null);
        Assertions.assertThatExceptionOfType(BadRequestApiException.class)
                .isThrownBy(() -> userService.saveUser(user))
                .withMessage(ApiErrors.USER_PASSWORD_EMPTY.getMessage());
    }

    @Test
    public void testSaveUserNullRole() {

        UserAddDto user = createSaveUser();
        user.setRole(null);
        Assertions.assertThatExceptionOfType(BadRequestApiException.class)
                .isThrownBy(() -> userService.saveUser(user))
                .withMessage(ApiErrors.USER_ROLE_EMPTY.getMessage());
    }

    @Test
    public void testSaveUserException() throws BadRequestApiException {

        Mockito.when(userDao.saveUser(Mockito.any())).thenThrow(new PersistenceException());

        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> userService.saveUser(createSaveUser()));
    }

    @Test
    public void testDeleteUser() throws BadRequestApiException {

        userService.deleteUser(1L);

        Mockito.verify(userDao, Mockito.times(1)).deleteUser(1L);
    }

    @Test
    public void testDeleteUserNullId() {

        Assertions.assertThatExceptionOfType(BadRequestApiException.class)
                .isThrownBy(() -> userService.deleteUser(null))
                .withMessage(ApiErrors.USER_ID_NEGATIVE.getMessage());
    }

    @Test
    public void testDeleteUserNegativeId() {

        Assertions.assertThatExceptionOfType(BadRequestApiException.class)
                .isThrownBy(() -> userService.deleteUser(-10L))
                .withMessage(ApiErrors.USER_ID_NEGATIVE.getMessage());
    }

    @Test
    public void testDeleteUserException() throws BadRequestApiException {

        Mockito.doThrow(new PersistenceException()).when(userDao).deleteUser(1L);

        Assertions.assertThatExceptionOfType(PersistenceException.class)
                .isThrownBy(() -> userService.deleteUser(1L));
    }

    private UserDto createDbUser1() {

        UserDto user = new UserDto();
        user.setId(11L);
        user.setUsername("johnny.english");
        user.setPasswordHash("hashed:bestjohnny");
        user.setRole(Role.ADMINISTRATOR);
        return user;
    }

    private UserDto createDbUser2() {

        UserDto user = new UserDto();
        user.setId(22L);
        user.setUsername("jon.snow");
        user.setPasswordHash("encrypted:777777");
        user.setRole(Role.EDITOR);
        return user;
    }

    private UserAddDto createSaveUser() {

        UserAddDto user = new UserAddDto();
        user.setUsername("john.wick");
        user.setPassword("bestfrienddaisy");
        user.setRole(Role.EDITOR);
        return user;
    }
}
