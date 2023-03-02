package jp.co.axa.apidemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.BasicAuth;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

/**
 * A springfox swagger configuration. Provides api information and limits exposed controllers to our package and a
 * button for basic authentication.
 *
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket docket() {

        List<SecurityReference> reference = Collections.singletonList(SecurityReference.builder().reference("basic")
                .scopes(new AuthorizationScope[0]).build());

        return new Docket(DocumentationType.SWAGGER_2)
                .securityContexts(Collections.singletonList(SecurityContext.builder().securityReferences(reference).build()))
                .securitySchemes(Collections.singletonList(new BasicAuth("basic")))
                .apiInfo(apiInfo()).select().apis(RequestHandlerSelectors.basePackage("jp.co.axa.apidemo.controllers"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("api-demo").description("Demo project for Spring Boot")
                .termsOfServiceUrl("https://www.axa.co.jp/").build();
    }
}
