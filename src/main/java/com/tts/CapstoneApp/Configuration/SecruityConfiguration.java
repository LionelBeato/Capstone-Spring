package com.tts.CapstoneApp.Configuration;

import com.tts.CapstoneApp.Repository.UserRepository;
import com.tts.CapstoneApp.Service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecruityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserServiceImpl oAuth2UserService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        Set<String> googleScopes = new HashSet<>();
        googleScopes.add(
                "https://www.googleapis.com/auth/userinfo.email");
        googleScopes.add(
                "https://www.googleapis.com/auth/userinfo.profile");


        oAuth2UserService.setAccessibleScopes(googleScopes);

        http

                .cors()
                .and()
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/console/**").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/users/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                // this will force oauth2 to use this loginpage for authentication
                .loginPage("/")
//                .loginProcessingUrl("/")
                .userInfoEndpoint()
                .oidcUserService(oAuth2UserService)
                .and()
                .successHandler((request, response, authentication) -> {

                    //Casted using OAuth2User interface instead of CustomOAuth2User class
                    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                    System.out.println(oAuth2User);

                    oAuth2UserService.processOAuthPostLogin(oAuth2User.getAttribute("sub"));
                    response.sendRedirect("https://erin-frontend.herokuapp.com/");
                })
                .and()
                .logout()
                .logoutSuccessUrl("https://erin-frontend.herokuapp.com/");


    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "https://erin-frontend.herokuapp.com/",
                "https://erin-frontend.herokuapp.com/"
                ));
        config.addAllowedHeader("Content-Type");
        config.addAllowedHeader("x-xsrf-token");
        config.addAllowedHeader("Authorization");
        config.addAllowedHeader("Access-Control-Allow-Headers");
        config.addAllowedHeader("Origin");
        config.addAllowedHeader("Accept");
        config.addAllowedHeader("X-Requested-With");
        config.addAllowedHeader("Access-Control-Request-Method");
        config.addAllowedHeader("Access-Control-Request-Headers");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


}

