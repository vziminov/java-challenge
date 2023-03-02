package jp.co.axa.apidemo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties("axa.apidemo")
public class AppProperties {

    private String excrypterSecretFile;

    private String adminPasswordFile;

}
