package net.mikoto.aozora.controller;

import com.alibaba.fastjson2.JSONObject;
import net.mikoto.aozora.model.Artwork;
import net.mikoto.aozora.service.ArtworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;

/**
 * @author mikoto
 * &#064;date 2023/11/19
 * Create for aozora
 */
@Controller
public class H5Controller {
    private final ArtworkService artworkService;
    private final ArtworkRestController artworkRestController;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh-mm");

    @Autowired
    public H5Controller(ArtworkService artworkService, ArtworkRestController artworkRestController) {
        this.artworkService = artworkService;
        this.artworkRestController = artworkRestController;
    }

    @RequestMapping("/artwork/{artworkId}")
    public ModelAndView artwork(@PathVariable int artworkId) {
        Artwork artwork = artworkService.getRemoteArtwork(artworkId);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("artwork");
        modelAndView.addObject("artwork", artwork);
        modelAndView.addObject("formattedCreateTime", DATE_FORMAT.format(artwork.getCreateTime()));

        return modelAndView;
    }

    @RequestMapping("/taskManager")
    public ModelAndView taskManager() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("taskManager");
//        modelAndView.addObject("artwork", artwork);
//        modelAndView.addObject("formattedCreateTime", DATE_FORMAT.format(artwork.getCreateTime()));

        return modelAndView;
    }

    @RequestMapping("/search")
    public ModelAndView search(@RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "keys", defaultValue = "") String keys,
                               @RequestParam(value = "column", defaultValue = "bookmark_count") String column,
                               @RequestParam(value = "type", defaultValue = "desc") String type,
                               @RequestParam(value = "grading", defaultValue = "0") int grading) {
        JSONObject result = artworkRestController.getArtworksByKeys(page, keys, column, type, grading);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("search");
        modelAndView.addObject("artworks", result.getJSONArray("body").toArray(Artwork.class));
        modelAndView.addObject("pagedArtworksCountTime", result.getString("timeCost"));
        modelAndView.addObject("artworksCount", artworkService.getCachedArtworksCount());
        modelAndView.addObject("artworksCountTime", 0);

        return modelAndView;
    }

    @RequestMapping("/")
    public ModelAndView index(@RequestParam(value = "page", defaultValue = "1") int page,
                              @RequestParam(value = "keys", defaultValue = "") String keys,
                              @RequestParam(value = "column", defaultValue = "bookmark_count") String column,
                              @RequestParam(value = "type", defaultValue = "desc") String type,
                              @RequestParam(value = "grading", defaultValue = "0") int grading) {

        return search(page, keys, column, type, grading);
    }
}
