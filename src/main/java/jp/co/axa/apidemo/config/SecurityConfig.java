package jp.co.axa.apidemo.config;

import jp.co.axa.apidemo.dao.UserDao;
import jp.co.axa.apidemo.dto.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;
import java.util.List;

/**
 * Configuration file for spring security related beans
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // according to OWASP recommendations
    private static final int ARGON2_SALT_LENGTH = 16;
    private static final int ARGON2_HASH_LENGTH = 32;
    private static final int ARGON2_PARALLELISM = 1;
    private static final int ARGON2_MEMORY = 12288;
    private static final int ARGON2_ITERATIONS = 3;

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new Argon2PasswordEncoder(ARGON2_SALT_LENGTH, ARGON2_HASH_LENGTH, ARGON2_PARALLELISM,
                ARGON2_MEMORY, ARGON2_ITERATIONS);
    }

    @Bean
    public UserDetailsService userDetailsService(UserDao userDao) {

        return username -> userDao.getUser(username)
                .map(dto -> new User(dto.getUsername(), dto.getPasswordHash(), createAuthority(dto.getRole().name())))
                .orElseThrow(() -> new UsernameNotFoundException("No user exist with username " + username));
    }

    @Bean
    public DaoAuthenticationProvider getDaoAuthProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider dbAuthProvider) throws Exception {

        http.authorizeRequests()
                // permit unauthorised access to swagger
                .antMatchers("/swagger-ui.html", "/webjars/**", "/v2/api-docs", "/swagger-resources/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/employees/**")
                    .hasAnyRole(Role.VIEWER.name(), Role.EDITOR.name(), Role.ADMINISTRATOR.name())
                .antMatchers( "/api/v1/employees/**").hasAnyRole(Role.EDITOR.name(), Role.ADMINISTRATOR.name())
                .antMatchers("/api/**").hasRole(Role.ADMINISTRATOR.name())
                .and().httpBasic()
                .and().authenticationManager(new ProviderManager(Collections.singletonList(dbAuthProvider)))
                .csrf().disable(); // no need for csrf in case of service - service communication
        return http.build();
    }

    private List<? extends GrantedAuthority> createAuthority(String role) {

        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }
}
