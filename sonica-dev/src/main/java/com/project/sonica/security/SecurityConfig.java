package com.project.sonica.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // modern replacement
public class SecurityConfig {
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	private final CustomAuthenticationSuccessHandler successHandler;

	public SecurityConfig(CustomAuthenticationSuccessHandler successHandler) {
		this.successHandler = successHandler;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter,
			CustomAccessDeniedHandler accessDeniedHandler, CustomAuthenticationEntryPoint authenticationEntryPoint)
			throws Exception {
		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						// Public endpoints (no JWT required)
						// Springdoc defaults to `/v3/api-docs/**`. Some environments/tools override it to `/v3/docs/**`.
						.requestMatchers("/api/auth/**", "/v3/api-docs/**", "/v3/docs/**", "/swagger-ui/**", "/swagger-ui.html")
						.permitAll()
						.requestMatchers("/api/admin/**").hasRole("ADMIN").requestMatchers("/api/customers/**")
						.hasRole("CUSTOMER").requestMatchers("/api/photographers/**").hasRole("PHOTOGRAPHER")
						.requestMatchers("/api/bookings/**").hasAnyRole("CUSTOMER", "ADMIN").anyRequest()
						.authenticated())
				.exceptionHandling(ex -> ex.accessDeniedHandler(accessDeniedHandler)
						.authenticationEntryPoint(authenticationEntryPoint))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.formLogin(form -> form.loginPage("/auth/login") // custom login page if you want
						.successHandler(successHandler) // ✅ redirect based on role
						.permitAll())
				.logout(Customizer.withDefaults());

		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}

//public class SecurityConfig {
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    // expose AuthenticationManager
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//        return configuration.getAuthenticationManager();
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {
//        http.csrf(csrf -> csrf.disable())
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/api/auth/**").permitAll()
//                .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                .requestMatchers("/api/customers/**").hasRole("CUSTOMER")
//                .requestMatchers("/api/photographers/**").hasRole("PHOTOGRAPHER")
//                .requestMatchers("/api/bookings/**").hasAnyRole("CUSTOMER", "ADMIN")
//                .anyRequest().authenticated()
//            )
//            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }
//}
//public class SecurityConfig {
//
//	@Bean
//	public AuthenticationManager authenticationManagerBean() throws Exception {
//		return authenticationManagerBean();
//	}
//
//	@Bean
//	public SecurityFilterChain filetrChain(HttpSecurity http) throws Exception {
//		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth.requestMatchers("/api/auth/**").permitAll()
//				.requestMatchers("/api/admin/**").hasRole("ADMIN").requestMatchers("/api/customers/**")
//				.hasRole("CUSTOMER").requestMatchers("/api/photographers/**").hasRole("PHOTOGRAPHER")
//				.requestMatchers("/api/bookings/**").hasAnyRole("CUSTOMER", "ADMIN").anyRequest().authenticated())
//				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//		return http.build();
//	}
//
//	@Bean
//	public PasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
//
////	protected void configure(HttpSecurity http) throws Exception {
////		http.csrf().disable().authorizeRequests().antMatchers("/api/auth/**").permitAll() // login/register open
////				.antMatchers("/api/admin/**").hasRole("ADMIN").antMatchers("/api/customers/**")
////				.hasAnyRole("CUSTOMER", "ADMIN").anyRequest().authenticated().and().sessionManagement()
////				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
////
////		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
////	}
//}
