package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.exceptions.ApiErrors;
import jp.co.axa.apidemo.exceptions.BadRequestApiException;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility class to accumulate all possible input parameter validations
 *
 */
public class ValidationUtils {


    /**
     * Validates an object to be not null
     *
     * @param object object to validate
     * @param error ApiError enum in case validation fails
     * @param <T> any object type
     * @throws BadRequestApiException if validation fails
     */
    public static <T> void validateNotNull(T object, ApiErrors error) throws BadRequestApiException {

        if (object == null)
            throw new BadRequestApiException(error);
    }

    /**
     * Validates a string to present and be not empty
     *
     * @param text string to validate
     * @param error ApiError enum in case validation fails
     * @throws BadRequestApiException if validation fails
     */
    public static void validateNotEmpty(String text, ApiErrors error) throws BadRequestApiException {

        if (StringUtils.isEmpty(text))
            throw new BadRequestApiException(error);
    }

    /**
     * Validates Integer to present and be positive number
     *
     * @param number integer to validate
     * @param error ApiError enum in case validation fails
     * @throws BadRequestApiException if validation fails
     */
    public static void validatePositive(Integer number, ApiErrors error) throws BadRequestApiException {

        if (number == null || number <= 0)
            throw new BadRequestApiException(error);
    }

    /**
     * Validates Long to present and be positive number
     *
     * @param number long to validate
     * @param error ApiError enum in case validation fails
     * @throws BadRequestApiException if validation fails
     */
    public static void validatePositive(Long number, ApiErrors error) throws BadRequestApiException {

        if (number == null || number <= 0)
            throw new BadRequestApiException(error);
    }
}
