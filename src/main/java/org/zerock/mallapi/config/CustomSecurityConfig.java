package org.zerock.mallapi.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.zerock.mallapi.security.filter.JWTCheckFilter;
import org.zerock.mallapi.security.handler.APILoginFailHandler;
import org.zerock.mallapi.security.handler.APILoginSuccessHandler;
import org.zerock.mallapi.security.handler.CustomAccessDeniedHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
@RequiredArgsConstructor
@EnableMethodSecurity
public class CustomSecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }


  // 보안 필터 체인을 설정하는 메서드입니다.
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    
    log.info("---------------------security config---------------------------");

    // CORS 설정을 추가합니다.
    http.cors(httpSecurityCorsConfigurer -> {
      httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
    });

    // 세션 관리를 설정합니다.
    http.sessionManagement(sessionConfig ->  sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    // CSRF 보호를 비활성화합니다.
    http.csrf(config -> config.disable());

    // 폼 로그인 설정을 추가합니다.
    http.formLogin(config -> {
      config.loginPage("/api/member/login");
      config.successHandler(new APILoginSuccessHandler());
      config.failureHandler(new APILoginFailHandler());
    });

    // JWT 체크 필터를 추가합니다.
    http.addFilterBefore(new JWTCheckFilter(), UsernamePasswordAuthenticationFilter.class); //JWT체크 

    // 예외 처리를 설정합니다.
    http.exceptionHandling(config -> {
      config.accessDeniedHandler(new CustomAccessDeniedHandler());
    });


    return http.build();
  }




  @Bean
  public CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOriginPatterns(Arrays.asList("*"));
    configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE"));
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
  
}
