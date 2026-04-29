package com.kolmir.subscription_service.security;

import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kolmir.subscription_service.model.CurrentUser;

import static com.kolmir.subscription_service.util.SubscriptionServiceConstants.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class UserHeadersFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String idHeader = request.getHeader(ID_HEADER);
        String roleHeader = request.getHeader(ROLE_HEADER);

        if (!isUserDataValid(idHeader, roleHeader)) {
            filterChain.doFilter(request, response);
        }

        Long userId = Long.valueOf(idHeader);
        CurrentUser user = new CurrentUser(userId, roleHeader);

        UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                            user, 
                            null, 
                            List.of(new SimpleGrantedAuthority(user.role()))
                        );

        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }

    private boolean isUserDataValid(String idHeader, String roleHeader) {
        if (!StringUtils.hasText(idHeader) || !StringUtils.hasText(roleHeader))
            return false;
        try {
            Long.parseLong(idHeader);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
