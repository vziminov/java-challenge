package jp.co.axa.apidemo.dto;

import io.swagger.annotations.ApiModelProperty;
import jp.co.axa.apidemo.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A DTO with basic inherited User information
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBaseDto {

    public final static String ADMIN_USERNAME = "admin";
    public final static String ADMIN_PASSWORD = "admin";

    @ApiModelProperty(value = "Username", required = true, example = "john.vick")
    private String username;

    @ApiModelProperty(value = "User role", required = true, example = "VIEWER")
    private Role role;

    /**
     * Maps this DTO back to the User entity
     *
     * @return User entity
     */
    public User toEntity() {

        User user = new User();
        user.setUsername(username);
        user.setRole(role);
        return user;
    }
}
