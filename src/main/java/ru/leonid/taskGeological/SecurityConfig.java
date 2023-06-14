package ru.leonid.taskGeological;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.leonid.taskGeological.Model.User;
import ru.leonid.taskGeological.Service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    @Autowired
    UserService userService;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.sessionManagement(smCustom -> smCustom.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        httpSecurity.authorizeHttpRequests(request -> request.anyRequest().authenticated()).httpBasic(Customizer.withDefaults());
        return httpSecurity
                .csrf(csrf-> csrf.ignoringRequestMatchers("/import/**"))
                .build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Создаем test-пользователя
    @Bean
    public CommandLineRunner commandLineRunner(){
        return (args)->{
            User user = new User();
            user.setUsername("Leonid");
            user.setPassword("12345");
            userService.saveUser(user);
        };
    }


}
