package com.mikedevcol.auth_service.interceptors;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.mikedevcol.auth_service.exception.ActiveSessionViolationException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtSessionInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    Cookie[] cookies = request.getCookies();
    if (cookies != null) {

      for (Cookie cookie : cookies) {

        if (cookie.getName().equals("accessToken") && cookie.getValue() != null) {

          throw new ActiveSessionViolationException("Active session detected. Please log out before logging in again.");
        }
      }
    }
    return true;
  }

}
