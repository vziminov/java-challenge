package jp.co.axa.apidemo.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when internal server error happened
 *
 */
@Getter
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ServerErrorApiException extends Exception {


    /**
     * Constructs a new ApiException with the specified ApiErrors instance
     *
     * @param error specific ApiErrors situation
     */
    public ServerErrorApiException(ApiErrors error) {

        super(error.getMessage());
    }

    /**
     * Constructs a new ApiException with the specified ApiErrors instance and throwable cause
     *
     * @param error specific ApiErrors situation
     * @param cause throwable cause of this exception
     */
    public ServerErrorApiException(ApiErrors error, Throwable cause) {

        super(error.getMessage(), cause);
    }
}
