package net.mikoto.aozora.controller;

import com.alibaba.fastjson2.JSONObject;
import net.mikoto.aozora.model.Artwork;
import net.mikoto.aozora.service.ArtworkService;
import net.mikoto.aozora.service.SearchService;
import net.mikoto.aozora.utils.TimeCost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author mikoto
 * &#064;date 2023/11/17
 * Create for aozora
 */
@RestController
@RequestMapping("/api/artwork")
public class ArtworkRestController {
    private final ArtworkService artworkService;
    private final SearchService searchService;

    @Autowired
    public ArtworkRestController(
            @Qualifier("CachedArtworkService") ArtworkService artworkService,
            SearchService searchService
    ) {
        this.artworkService = artworkService;
        this.searchService = searchService;
    }

    @RequestMapping("/getRemoteArtwork/{artworkId}")
    public JSONObject getRemoteArtwork(@PathVariable final int artworkId,
                                       @RequestParam(defaultValue = "false") final boolean isSave) {
        JSONObject result = new JSONObject();
        result.put(
                "timeCost",
                ((TimeCost) () -> {
                    Artwork artwork = artworkService.getRemoteArtwork(artworkId);
                    result.put("body", JSONObject.from(artwork));

                    if (isSave && artwork != null) {
                        Artwork storagedArtwork = artworkService.getById(artwork.getArtworkId());
                        if (storagedArtwork == null) {
                            artworkService.save(artwork);
                        } else {
                            artworkService.updateById(artwork);
                        }
                    }

                }).getTimeCost()
        );
        return result;
    }

    @RequestMapping("/getArtworksCount")
    public JSONObject getArtworksCount() {
        JSONObject result = new JSONObject();
        result.put(
                "timeCost",
                ((TimeCost) () -> result.put("body", searchService.artworkCount())).getTimeCost()
        );
        return result;
    }

    @RequestMapping("/getArtworks/{key}/{page}")
    public JSONObject getArtworks(@PathVariable final String key,
                                  @PathVariable final int page,
                                  @RequestParam(defaultValue = "bookmarkCount") final String orderingColumn,
                                  @RequestParam(defaultValue = "desc") final String orderingType,
                                  @RequestParam(defaultValue = "1") final int grading,
                                  @RequestParam(defaultValue = "false") final boolean isAi,
                                  @RequestParam(defaultValue = "false") final boolean isManga) {
        JSONObject result = new JSONObject();


        result.put(
                "timeCost",
                ((TimeCost) () -> {
                    List<Integer> artworkIds = searchService.search(key, grading, isAi, isManga, orderingType, orderingColumn, 12, page);
                    Artwork[] artworks = artworkService.listByIds(artworkIds).toArray(new Artwork[0]);
                    result.put("body", Artwork.orderingArtwork(orderingColumn, artworks));
                }).getTimeCost()
        );
        return result;
    }

    @RequestMapping("/getArtwork/{artworkId}")
    public JSONObject getArtwork(@PathVariable int artworkId) {
        JSONObject result = new JSONObject();
        result.put("timeCost", ((TimeCost) () ->
                result.put("body", artworkService.getById(artworkId))
        ).getTimeCost());

        return result;
    }
}
