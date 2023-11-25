package net.mikoto.aozora.controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlInjectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.mikoto.aozora.model.Artwork;
import net.mikoto.aozora.service.ArtworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Objects;

/**
 * @author mikoto
 * &#064;date 2023/11/17
 * Create for aozora
 */
@RestController
@RequestMapping("/api/artwork")
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
        Date startDate = new Date();
        JSONObject result = new JSONObject();
        Artwork artwork = artworkService.getRemoteArtwork(artworkId);
        artworkService.saveOrUpdate(artwork);
        result.put("success", true);
        result.put("msg", "");
        result.put("body", JSONObject.from(artwork));
        Date endDate = new Date();
        result.put("timeCost", ((double) (endDate.getTime() - startDate.getTime()) / 1000.00) + "s");
        return result;
    }

    @RequestMapping("/getArtworksCount")
    public JSONObject getArtworksCount() {
        Date startDate = new Date();
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("msg", "");
        result.put("body", artworkService.count());
        Date endDate = new Date();
        result.put("timeCost", ((double) (endDate.getTime() - startDate.getTime()) / 1000.00) + "s");
        return result;
    }

    @RequestMapping("/getArtworksByKeys")
    public JSONObject getArtworksByKeys(@RequestParam int page,
                                        @RequestParam String keys,
                                        @RequestParam String column,
                                        @RequestParam String type,
                                        @RequestParam int grading) {
        if ("".equals(keys)) {
            return getArtworks(page, column, type, grading);
        }
        Date startDate = new Date();
        JSONObject result = new JSONObject();
        if (SqlInjectionUtils.check(column) || SqlInjectionUtils.check(keys)) {
            result.put("success", false);
            result.put("msg", "Injections in column or tags");
            return result;
        }
        QueryWrapper<Artwork> queryWrapper = new QueryWrapper<>();
        queryWrapper.inSql("grading", "select grading where grading <= " + grading);
        queryWrapper.like("tags", keys).or().like("artwork_title", keys);

        Page<Artwork> artworkPage = new Page<>(page, 12);
        artworkPage.addOrder(new OrderItem(column, "asc".equals(type)));
        Page<Artwork> resultArtworkPage = artworkService.page(artworkPage, queryWrapper);

        if (resultArtworkPage.getSize() == 0) {
            result.put("success", false);
            result.put("msg", "Page is out of index or unaviliable grading or column");
        } else {
            result.put("success", true);
            result.put("msg", "");
        }
        result.put("body", resultArtworkPage.getRecords());
        Date endDate = new Date();
        result.put("timeCost", ((double) (endDate.getTime() - startDate.getTime()) / 1000.00) + "s");
        return result;
    }

    @RequestMapping("/getArtworks")
    public JSONObject getArtworks(@RequestParam int page,
                                  @RequestParam String column,
                                  @RequestParam String type,
                                  @RequestParam int grading) {
        Date startDate = new Date();
        JSONObject result = new JSONObject();
        if (SqlInjectionUtils.check(column)) {
            result.put("success", false);
            result.put("msg", "Injections in column");
            return result;
        }
        QueryWrapper<Artwork> queryWrapper = new QueryWrapper<>();
        queryWrapper.le("grading", grading + 1);

        Page<Artwork> artworkPage = new Page<>(page, 12);
        artworkPage.addOrder(new OrderItem(column, "asc".equals(type)));
        Page<Artwork> resultArtworkPage = artworkService.page(artworkPage, queryWrapper);

        if (resultArtworkPage.getSize() == 0) {
            result.put("success", false);
            result.put("msg", "Page is out of index or unaviliable grading or column");
        } else {
            result.put("success", true);
            result.put("msg", "");
        }
        result.put("body", resultArtworkPage.getRecords());
        Date endDate = new Date();
        result.put("timeCost", ((double) (endDate.getTime() - startDate.getTime()) / 1000.00) + "s");
        return result;
    }

    @RequestMapping("/getArtwork/{artworkId}")
    public JSONObject getArtwork(@PathVariable int artworkId) {
        Date startDate = new Date();
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("msg", "");
        result.put("body", artworkService.getById(artworkId));
        Date endDate = new Date();
        result.put("timeCost", ((double) (endDate.getTime() - startDate.getTime()) / 1000.00) + "s");
        return result;
    }

    @RequestMapping(
            value = "/getImage",
            produces = "image/png"
    )
    public byte[] getArtworkImage(@RequestParam String url) {
        return artworkService.getImage(url);
    }
}
