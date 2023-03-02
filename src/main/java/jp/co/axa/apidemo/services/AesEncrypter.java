package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.AppProperties;
import jp.co.axa.apidemo.exceptions.ApiErrors;
import jp.co.axa.apidemo.exceptions.ServerErrorApiException;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * An Encrypter implementation that uses AES GCM algorithm with 192 bit key length.<br>
 * For JRE8 with addition of 'Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy'
 * 256 bit keys could be used as well.
 *
 */
@Log
@Service
public class AesEncrypter implements Encrypter {

    private static final String AES_GCM = "AES/GCM/NoPadding";
    private static final int AUTH_TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;
    private static final String DEFAULT_KEY = "rAVn/aQefln0FxbdOOp9jRWmZHksz4mZ";

    private final Base64.Decoder decoder = Base64.getDecoder();
    private final Base64.Encoder encoder = Base64.getEncoder();

    private final SecretKeySpec sks;

    /**
     * Simple Spring constructor with injection.<br>
     * Tries to encryption empty string to fail-fast at spring start-up if key is incorrect.<br>
     * Example: XtqzpMD8ZP1Ysb8y$j6MiYYMmI48oyVJ7vkAVCBVHEAbEb4zuX6vI7qsLqJtN
     *
     * @param appProperties injected AppProperties bean
     * @throws IOException if unable to read secret key file
     * @throws ServerErrorApiException if encryption is not working
     */
    public AesEncrypter(AppProperties appProperties) throws IOException, ServerErrorApiException {

        String secretFile = appProperties.getExcrypterSecretFile();
        // no secret provided
        if (StringUtils.isEmpty(secretFile)) {
            sks = new SecretKeySpec(decoder.decode(DEFAULT_KEY), "AES");
        } else {
            Path secretPath = Paths.get(secretFile);
            // when external path is provided, but incorrect need to fail-fast
            if (!Files.exists(secretPath))
                throw new IllegalStateException("No external secret key file found in: " + secretFile);

            sks = new SecretKeySpec(Files.readAllBytes(secretPath), "AES");
        }
        encrypt("");
    }

    /**
     * Encrypts provided plaintext data with AES/GCM/NoPadding with randomly generated IV.<br>
     * Result is composed of base64 encoded IV and encrypted string with '$' used as separator.<br>
     * Example: XtqzpMD8ZP1Ysb8y$j6MiYYMmI48oyVJ7vkAVCBVHEAbEb4zuX6vI7qsLqJtN
     *
     * @param plaintext plaintext secret to encrypt
     * @return encrypted data with IV
     * @throws ServerErrorApiException if unable to encrypt data
     */
    @Override
    public String encrypt(String plaintext) throws ServerErrorApiException {

        try {
            Cipher cipher = Cipher.getInstance(AES_GCM);
            SecureRandom random = new SecureRandom();
            // for AES/GCM it's crucial to create random IV each time
            byte[] iv = new byte[IV_LENGTH];
            random.nextBytes(iv);
            GCMParameterSpec paramSpec = new GCMParameterSpec(AUTH_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, sks, paramSpec);
            byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return encoder.encodeToString(iv) + "$" + encoder.encodeToString(cipherText);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new ServerErrorApiException(ApiErrors.ENCRYPTION_FAILED, e);
        }
    }

    /**
     * Decrypts provided encrypted data.<br>
     * Expected format is base64 encoded IV and encrypted string with '$' used as separator.
     *
     * @param encrypted encrypted data with IV
     * @return plaintext data
     * @throws ServerErrorApiException if unable to decrypt data
     */
    @Override
    public String decrypt(String encrypted) throws ServerErrorApiException {

        String[] splitted = encrypted.split("\\$");
        if (splitted.length != 2)
            throw new ServerErrorApiException(ApiErrors.DECRYPTION_FAILED);
        try {
            Cipher cipher = Cipher.getInstance(AES_GCM);
            byte[] iv = decoder.decode(splitted[0]);
            GCMParameterSpec paramSpec = new GCMParameterSpec(AUTH_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, sks, paramSpec);
            byte[] cipherText = cipher.doFinal(decoder.decode(splitted[1]));
            return new String(cipherText, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new ServerErrorApiException(ApiErrors.DECRYPTION_FAILED, e);
        }
    }
}
