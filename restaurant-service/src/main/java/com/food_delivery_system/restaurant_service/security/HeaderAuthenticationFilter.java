package com.food_delivery_system.restaurant_service.security;

import com.food_delivery_system.restaurant_service.enums.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_EMAIL = "X-User-Email";
    public static final String HEADER_USER_ROLE = "X-User-Role";

    @Override
    protected void doFilterInternal (
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String userId = request.getHeader(HEADER_USER_ID);
        String userEmail = request.getHeader(HEADER_USER_EMAIL);
        String roleHeader = request.getHeader(HEADER_USER_ROLE);

        if (userId != null && userEmail != null && roleHeader != null) {
            try {
                Role role = Role.valueOf(roleHeader);
                AuthenticatedUser principal = new AuthenticatedUser(userId, userEmail, role);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (IllegalArgumentException e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
