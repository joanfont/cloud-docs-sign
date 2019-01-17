package com.joanfont.clouddocssign.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http
                .antMatcher("/**")
                .authorizeRequests()
                .antMatchers(
                        "/login",
                        "/webjars/**",
                        "/css/**",
                        "/images/**",
                        "/fonts/**",
                        "/js/**",
                        "/vendor/**"
                )
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .and()
                .logout()
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID")
                .and()
                .oauth2Login()
                .loginPage("/login");
    }
}