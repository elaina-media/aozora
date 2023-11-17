package net.mikoto.aozora.controller;

import com.alibaba.fastjson2.JSONObject;
import net.mikoto.aozora.model.Artwork;
import net.mikoto.aozora.service.ArtworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mikoto
 * &#064;date 2023/11/17
 * Create for aozora
 */
@RestController
@RequestMapping("/artwork")
public class ArtworkRestController {
    private final ArtworkService artworkService;

    @Autowired
    public ArtworkRestController(ArtworkService artworkService) {
        this.artworkService = artworkService;
    }

    @RequestMapping("/getRemoteArtwork")
    public JSONObject getRemoteArtwork(@RequestParam int artworkId) {
        return JSONObject.from(artworkService.getRemoteArtwork(artworkId));
    }
    @RequestMapping("/getRemoteArtworkAndSave")
    public JSONObject getRemoteArtworkAndSave(@RequestParam int artworkId) {
        Artwork artwork = artworkService.getRemoteArtwork(artworkId);
        artworkService.saveOrUpdate(artwork);
        return JSONObject.from(artwork);
    }
}
