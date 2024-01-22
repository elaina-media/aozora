package net.mikoto.aozora.canalclient.controller;

import net.mikoto.aozora.canalclient.mapper.ArtworkIndexMapper;
import net.mikoto.aozora.model.Artwork;
import net.mikoto.aozora.model.ArtworkIndex;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author mikoto
 * &#064;date 2024/1/17
 * Create for aozora
 */
@RestController
public class ArtworkIndexRestController {
    private final ArtworkIndexMapper artworkIndexMapper;

    public ArtworkIndexRestController(ArtworkIndexMapper artworkIndexMapper) {
        this.artworkIndexMapper = artworkIndexMapper;
    }

    @RequestMapping("/update")
    public String update(String artworkIndexId, int artworkId, int grading, int bookmarkCount, int likeCount, int viewCount) {
        Date startDate = new Date();
        ArtworkIndex artworkIndex = new ArtworkIndex(Long.valueOf(artworkIndexId), artworkId, Artwork.Grading.getGrading(grading), bookmarkCount, likeCount, viewCount);
        artworkIndexMapper.insert(artworkIndex);
        String result = artworkId + ":" + artworkIndexId + ":" +  ((double) (new Date().getTime() - startDate.getTime()) / 1000.00) + "s";
        System.out.println(result);
        return result;
    }
}
