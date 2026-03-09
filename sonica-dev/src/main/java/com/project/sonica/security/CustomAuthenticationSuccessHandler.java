package com.project.sonica.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {
		String redirectUrl = "/";

		var authorities = authentication.getAuthorities();
		String role = authorities.iterator().next().getAuthority();

		if (role.equals("ROLE_ADMIN")) {
			redirectUrl = "/admin/dashboard";
		} else if (role.equals("ROLE_PHOTOGRAPHER")) {
			redirectUrl = "/photographer/dashboard";
		} else if (role.equals("ROLE_CUSTOMER")) {
			redirectUrl = "/customer/dashboard";
		}

		response.sendRedirect(redirectUrl);
	}
}
