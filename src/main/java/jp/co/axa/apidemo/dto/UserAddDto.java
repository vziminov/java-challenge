package jp.co.axa.apidemo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jp.co.axa.apidemo.entities.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * A DTO with User information needed to add new User
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ApiModel(value = "UserAddition", description = "User addition data")
public class UserAddDto extends UserBaseDto {

    @ApiModelProperty(value = "User password", required = true, example = "5CwTbz_Ag$Z!")
    private String password;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private String passwordHash;

    /**
     * Maps this DTO back to the User entity
     *
     * @return User entity
     */
    public User toEntity() {

        User user = super.toEntity();
        user.setPasswordHash(passwordHash);
        return user;
    }
}
