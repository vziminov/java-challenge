package jp.co.axa.apidemo.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when request is invalid
 *
 */
@Getter
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestApiException extends Exception {


    /**
     * Constructs a new ApiException with the specified ApiErrors instance
     *
     * @param error specific ApiErrors situation
     */
    public BadRequestApiException(ApiErrors error) {

        super(error.getMessage());
    }
}
