package net.mikoto.aozora.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.mikoto.aozora.service.ArtworkService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author mikoto
 * &#064;date 2024/2/2
 * Create for aozora
 */
@Component
public class ArtworkImageFilter implements HandlerInterceptor {
    @Resource
    private ArtworkService artworkService;

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws IOException {
        String url = request.getServletPath().replace("/api/artwork/getImage", "");
        byte[] image = artworkService.getImage(url);
        response.setContentType("image/png;image/jpeg;");
        response.getOutputStream().write(image);
        return false;
    }

}
