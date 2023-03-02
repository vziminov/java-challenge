package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.exceptions.ServerErrorApiException;

/**
 * An interface for providing encryption functionality for sensitive data
 *
 */
public interface Encrypter {

    /**
     * Encrypts provided plaintext data
     *
     * @param plaintext plaintext secret to encrypt
     * @return encrypted data
     * @throws ServerErrorApiException if unable to encrypt data
     */
    String encrypt(String plaintext) throws ServerErrorApiException;

    /**
     * Decrypts provided encrypted data
     *
     * @param encrypted encrypted data to decrypt
     * @return plaintext data
     * @throws ServerErrorApiException if unable to decrypt data
     */
    String decrypt(String encrypted) throws ServerErrorApiException;
}
