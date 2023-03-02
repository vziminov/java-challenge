package jp.co.axa.apidemo.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jp.co.axa.apidemo.dto.UserAddDto;
import jp.co.axa.apidemo.dto.UserDto;
import jp.co.axa.apidemo.exceptions.BadRequestApiException;
import jp.co.axa.apidemo.exceptions.NotFoundApiException;
import jp.co.axa.apidemo.services.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "User")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {

        this.userService = userService;
    }

    @ApiOperation(value = "Get Users", notes = "Get all Users data")
    @GetMapping("/get")
    public List<UserDto> getUsers() {

        return userService.retrieveUsers();
    }

    @ApiOperation(value = "Get User", notes = "Get User data with specified id")
    @GetMapping("/get/{userId}")
    public UserDto getUser(@ApiParam(name =  "userId", value = "User id", example = "1", required = true)
                                   @PathVariable(name="userId") Long userId)
            throws NotFoundApiException, BadRequestApiException {

        return userService.getUser(userId);
    }

    @ApiOperation(value = "Add User", notes = "Adds User with provided information")
    @PostMapping("/add")
    public Long saveUser(@RequestBody UserAddDto user) throws BadRequestApiException {

        return userService.saveUser(user);
    }

    @ApiOperation(value = "Delete User", notes = "Deletes User with specified id")
    @DeleteMapping("/delete/{userId}")
    public void deleteUser(@ApiParam(name =  "userId", value = "User id", example = "1", required = true)
                               @PathVariable(name="userId") Long userId) throws BadRequestApiException {

        userService.deleteUser(userId);
    }

}
