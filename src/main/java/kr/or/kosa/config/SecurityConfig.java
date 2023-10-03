package kr.or.kosa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import kr.or.kosa.config.jwt.JwtAuthenticationFilter;
import kr.or.kosa.config.jwt.JwtAuthorizationFilter;
import kr.or.kosa.repository.UserRepository;

// https://github.com/spring-projects/spring-security/issues/10822 참고
@Configuration
@EnableWebSecurity // 시큐리티 활성화 -> 기본 스프링 필터체인에 등록
public class SecurityConfig {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CorsConfig corsConfig;

	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable);
		
		AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
		System.out.println("authenticationManager->"+authenticationManager);
		//세션 정책 상수 설정
		/*
		 SessionCreationPolicy.ALWAYS      - 스프링시큐리티가 항상 세션을 생성
	     SessionCreationPolicy.IF_REQUIRED - 스프링시큐리티가 필요시 생성(기본) 
	     SessionCreationPolicy.NEVER       - 스프링시큐리티가 생성하지않지만, 기존에 존재하면 사용
	     SessionCreationPolicy.STATELESS   - 스프링시큐리티가 생성하지도않고 기존것을 사용하지도 않음,
	                                         JWT 같은 토큰방식을 쓸때 사용하는 설정 
	     */
		http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable);
			
		http.apply(new MyCustomDsl());// 커스텀 필터 등록

		http.authorizeHttpRequests(authroize -> 
			authroize.requestMatchers("/api/v1/user/**")
			.hasAnyAuthority("ROLE_USER", "ROLE_MANAGER", "ROLE_ADMIN") //여러개의 권한 중 하나라도 있으면 성공 
			.requestMatchers("/api/v1/manager/**")
			.hasAnyAuthority("ROLE_MANAGER","ROLE_ADMIN")
			.requestMatchers("/api/v1/admin/**")
			.hasAnyAuthority("ROLE_ADMIN") //반드시 해당 권한만 허가  
			.anyRequest().permitAll() // /home url은 비회원이 사용할 수 있음 
		);
		
		return http.build();
	}

	public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
		@Override
		public void configure(HttpSecurity http) throws Exception {
			
			AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
			http.addFilter(corsConfig.corsFilter())
				//로그인 후 토큰을 발급하는 필더 
				.addFilter(new JwtAuthenticationFilter(authenticationManager))
				//발급된 토큰를 전달 받아 실행되는 URL 전에 
				//토큰을 이용하여 인증 객체를 생성하여 설정함 
				.addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository));
		}
	}

}
