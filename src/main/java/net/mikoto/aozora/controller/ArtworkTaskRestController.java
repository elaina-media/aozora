package net.mikoto.aozora.controller;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mikoto
 * &#064;date 2024/2/20
 * Create for aozora
 */
@RestController
@RequestMapping("/api/artworkTask")
@Log
public class ArtworkTaskRestController {
    @RequestMapping("/subscription")
    public JSONObject subscription() {
        return null;
    }

    
}
