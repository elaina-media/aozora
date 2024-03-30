package net.mikoto.aozora.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * @author mikoto
 * &#064;date 2024/2/20
 * Create for aozora
 */
@Component
public class CorsFilter implements HandlerInterceptor {
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        return true;
    }
}
