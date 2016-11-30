package se.kumliens.concept2runkeeper.security;

import se.kumliens.concept2runkeeper.repos.UserRepo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Created by svante on 2016-11-30.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/swagger-ui.html").fullyAuthenticated().and().formLogin()
                //                .loginPage("/login").failureUrl("/login?error").permitAll().and()
                //                .logout().permitAll()
                .and().csrf().disable();
    }

    @Bean MongoUserDetailsService mongoUserDetailsService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        return new MongoUserDetailsService(userRepo, passwordEncoder);
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
