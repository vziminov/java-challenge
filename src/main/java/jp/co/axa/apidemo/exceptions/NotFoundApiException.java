package jp.co.axa.apidemo.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when no record found with provided id
 *
 */
@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundApiException extends Exception {


    /**
     * Constructs a new ApiException with the specified ApiErrors instance
     *
     * @param error specific ApiErrors situation
     */
    public NotFoundApiException(ApiErrors error) {

        super(error.getMessage());
    }
}
