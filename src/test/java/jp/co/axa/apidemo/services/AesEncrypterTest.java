package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.AppProperties;
import jp.co.axa.apidemo.exceptions.ApiErrors;
import jp.co.axa.apidemo.exceptions.ServerErrorApiException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {AesEncrypter.class})
public class AesEncrypterTest {

    @Autowired
    private Encrypter encrypter;

    @MockBean
    private AppProperties appProperties;

    @Test
    public void testEncryptDecrypt() throws ServerErrorApiException {

        String text = "someStringWith123";
        String encrypted = encrypter.encrypt(text);

        String decrypted = encrypter.decrypt(encrypted);
        Assertions.assertThat(decrypted).isEqualTo(text);
    }

    @Test
    public void testTwoEncryptions() throws ServerErrorApiException {

        String text = "someOtherStringWith123";
        String encrypted1 = encrypter.encrypt(text);
        String encrypted2 = encrypter.encrypt(text);

        Assertions.assertThat(encrypted1).isNotEqualTo(encrypted2);;
    }

    @Test
    public void testEncryptDecryptNumber() throws ServerErrorApiException {

        String text = "10120000";
        String decrypted = encrypter.decrypt(encrypter.encrypt(text));
        Assertions.assertThat(decrypted).isEqualTo(text);
    }

    @Test
    public void testDecrypt() throws ServerErrorApiException {

        String encrypted = "XtqzpMD8ZP1Ysb8y$j6MiYYMmI48oyVJ7vkAVCBVHEAbEb4zuX6vI7qsLqJtN";
        String decrypted = encrypter.decrypt(encrypted);
        Assertions.assertThat(decrypted).isEqualTo("someStringWith123");
    }

    @Test
    public void testDecryptChangedVI() {

        // VI is changed
        String encrypted = "YtqzpMD8ZP1Ysb8y$j6MiYYMmI48oyVJ7vkAVCBVHEAbEb4zuX6vI7qsLqJtN";
        Assertions.assertThatExceptionOfType(ServerErrorApiException.class)
                .isThrownBy(() -> encrypter.decrypt(encrypted)).withMessage(ApiErrors.DECRYPTION_FAILED.getMessage());
    }

    @Test
    public void testDecryptOtherKey() {

        // encrypted with other secret key
        String encrypted = "sTS06gFyj51JbZUx$wq5E0lT4MoQBxjonS+kkibtWrvgFRgfNuUjLHuyC3IZLRz9DArw=";
        Assertions.assertThatExceptionOfType(ServerErrorApiException.class)
                .isThrownBy(() -> encrypter.decrypt(encrypted)).withMessage(ApiErrors.DECRYPTION_FAILED.getMessage());
    }

    @Test
    public void testDecryptIncorrectKey() {

        // encrypted string is separated into 3 parts with '$'
        String encrypted = "rvgFR$sTS06gFyj51JbZUx$wq5E0lT4MoQBxjonS+kkibtWrvgFRgfNuUjLHuyC3IZLRz9DArw=";
        Assertions.assertThatExceptionOfType(ServerErrorApiException.class)
                .isThrownBy(() -> encrypter.decrypt(encrypted)).withMessage(ApiErrors.DECRYPTION_FAILED.getMessage());
    }

    @Test
    public void testEncryptNonexistentKey() {

        Mockito.when(appProperties.getExcrypterSecretFile()).thenReturn("/notexistingpath/forsure");
        Assertions.assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> new AesEncrypter(appProperties));
    }

}
