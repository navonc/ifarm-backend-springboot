package com.ifarm.config;

import com.ifarm.common.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JWT认证过滤器
 * 
 * @author ifarm
 * @since 2025-01-19
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                  @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        log.debug("JWT Filter processing request: {} {}", request.getMethod(), requestURI);

        try {
            String token = extractTokenFromRequest(request);
            log.debug("Extracted token: {}", token != null ? "Present (length: " + token.length() + ")" : "Not found");

            if (StringUtils.hasText(token)) {
                log.debug("Validating token...");
                boolean isValid = jwtUtil.validateAccessToken(token);
                log.debug("Token validation result: {}", isValid);

                if (isValid) {
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    String username = jwtUtil.getUsernameFromToken(token);
                    Integer userType = jwtUtil.getUserTypeFromToken(token);

                    log.debug("Token parsed - userId: {}, username: {}, userType: {}", userId, username, userType);

                    if (userId != null && username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        // 根据用户类型设置角色
                        List<SimpleGrantedAuthority> authorities = getUserAuthorities(userType);

                        // 创建认证对象
                        UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, authorities);

                        // 设置详细信息
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // 设置到安全上下文
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        log.debug("JWT authentication successful for user: {}, userType: {}, authorities: {}",
                                 username, userType, authorities);
                    } else {
                        log.debug("Authentication skipped - userId: {}, username: {}, existing auth: {}",
                                 userId, username, SecurityContextHolder.getContext().getAuthentication() != null);
                    }
                } else {
                    log.debug("Token validation failed");
                }
            } else {
                log.debug("No token found in request");
            }
        } catch (Exception e) {
            log.error("JWT authentication failed: {}", e.getMessage(), e);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 根据用户类型获取权限列表
     *
     * @param userType 用户类型
     * @return 权限列表
     */
    private List<SimpleGrantedAuthority> getUserAuthorities(Integer userType) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER")); // 基础角色

        if (userType != null) {
            switch (userType) {
                case 2 -> authorities.add(new SimpleGrantedAuthority("ROLE_FARM_OWNER"));
                case 3 -> {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    authorities.add(new SimpleGrantedAuthority("ROLE_FARM_OWNER")); // 管理员包含农场主权限
                }
            }
        }
        return authorities;
    }

    /**
     * 从请求中提取JWT token
     *
     * @param request HTTP请求
     * @return JWT token
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        log.debug("Authorization header: {}", authHeader != null ? "Present" : "Not found");
        if (authHeader != null) {
            log.debug("Authorization header value: {}", authHeader.substring(0, Math.min(20, authHeader.length())) + "...");
        }
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        log.debug("Extracted token from header: {}", token != null ? "Success" : "Failed");
        return token;
    }

    /**
     * 判断是否跳过JWT验证
     * 
     * @param request HTTP请求
     * @return 是否跳过
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // 跳过认证接口
        if (path.startsWith("/api/auth/login") || 
            path.startsWith("/api/auth/wechat-login") || 
            path.startsWith("/api/auth/refresh")) {
            return true;
        }
        
        // 跳过静态资源和文档
        return path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/doc.html") ||
                path.startsWith("/webjars") ||
                path.startsWith("/favicon.ico");
    }
}
