package jp.co.axa.apidemo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jp.co.axa.apidemo.entities.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * A DTO representing User information including id
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ApiModel(value = "User", description = "User data")
public class UserDto extends UserBaseDto {

    @ApiModelProperty(value = "User id", required = true, example = "1")
    private Long id;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private String passwordHash;

    /**
     * A constructor that creates DTO based on the User entity
     *
     * @param user User entity
     */
    public UserDto(User user) {

        super(user.getUsername(), user.getRole());
        this.id = user.getId();
        this.passwordHash = user.getPasswordHash();
    }
}
