package com.kolmir.security.filter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kolmir.auth.model.CurrentUser;
import com.kolmir.security.util.CurrentUserUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class UserHeadersFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Optional<CurrentUser> optionalUser = CurrentUserUtil.extractCurrentUser(request);
        if (optionalUser.isEmpty())
            filterChain.doFilter(request, response);

        CurrentUser currentUser = optionalUser.get();
        UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                            currentUser, 
                            null, 
                            List.of(new SimpleGrantedAuthority("ROLE_" + currentUser.role()))
                        );

        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }
}
