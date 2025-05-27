package com.example.api.auth.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

public class JwtUtils {

  public static String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
