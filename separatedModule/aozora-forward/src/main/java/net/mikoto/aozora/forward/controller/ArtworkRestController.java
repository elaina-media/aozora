package net.mikoto.aozora.forward.controller;

import com.alibaba.fastjson2.JSONObject;
import net.mikoto.aozora.client.PixivClient;
import net.mikoto.aozora.forward.service.ArtworkService;
import net.mikoto.aozora.forward.service.AvailableRoute;
import net.mikoto.aozora.model.Artwork;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author mikoto
 * &#064;date 2023/11/28
 * Create for aozora
 */
@RestController
@RequestMapping("/artwork")
public class ArtworkRestController {
    private final PixivClient pixivClient;
    private final ArtworkService artworkService;
    private static final String[] COOKIES = new String[] {
            "100916322_qUikNqRqd3tyQaljYQ0IKK6aVpcIu96w",
            "100916389_Ou7APQ00NuPiJTgCjCmMHpIHEGhiNVEn",
            "100916420_Xu2XlbVG7ZwtLIZl7h5hY24wZItIoQw7",
            "100916458_qIAe5aSLIhztoiwbSFCxHhinsf2Lkz4i",
            "100916473_lmyfaP73DQtY4cWG0K91601xI2i13BGo",
            "100916501_z7xLABvGX2LBGUA2gaOFsKCd2KYwXill",
            "100916521_669daslJ21TVM2biH9tCNHqgJw0mxLXk"

    };
    private int cookieCount = 0;

    public ArtworkRestController(PixivClient pixivClient, ArtworkService artworkService) {
        this.pixivClient = pixivClient;
        this.artworkService = artworkService;
    }

    @RequestMapping("/getArtwork")
    public JSONObject getArtwork(@RequestParam int artworkId) {
        Date startDate = new Date();
        JSONObject result = new JSONObject();
        String artworkData = pixivClient.getArtwork(artworkId, "PHPSESSID=" + COOKIES[cookieCount]);
        Artwork artwork = (Artwork) artworkService.parseRawData(AvailableRoute.Artwork, JSONObject.parseObject(artworkData));
        cookieCount++;
        if (cookieCount >= COOKIES.length) {
            cookieCount = 0;
        }
        result.put("success", true);
        result.put("msg", "");
        result.put("body", JSONObject.from(artwork));
        Date endDate = new Date();
        result.put("timeCost", ((double) (endDate.getTime() - startDate.getTime()) / 1000.00) + "s");
        return result;
    }

    @RequestMapping(
            value = "/getImage",
            produces = "image/png"
    )
    public byte[] getArtworkImage(@RequestParam String url) {
        return pixivClient.getImage(url);
    }
}
