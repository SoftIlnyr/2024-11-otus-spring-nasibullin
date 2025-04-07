package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.otus.hw.security.UserRole;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String adminAuthority = UserRole.ADMIN.getAuthority();
        String authorAuthority = UserRole.AUTHOR.getAuthority();
        String readerAuthority = UserRole.READER.getAuthority();

        StringBuilder rolesBuilder = new StringBuilder();
        rolesBuilder.append("%s > %s\n".formatted(adminAuthority, authorAuthority));
        rolesBuilder.append("%s > %s\n".formatted(authorAuthority, readerAuthority));

        roleHierarchy.setHierarchy(rolesBuilder.toString());
        return roleHierarchy;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/login").anonymous()
                        .requestMatchers("/css/**", "/js/**").permitAll()
                        .requestMatchers("/authors/**", "/api/authors/**").authenticated()
                        .requestMatchers("/genres/**", "/api/genres/**").authenticated()
                        .requestMatchers("/books/**", "/api/books/**").authenticated()
                        .requestMatchers("/actuator/**", "/explorer/**").hasRole("ADMIN")
                        .requestMatchers("/datarest/**").hasAnyRole("ADMIN", "AUTHOR")
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .defaultSuccessUrl("/books", true) // Перенаправление после успешного логина
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
