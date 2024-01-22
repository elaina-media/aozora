package net.mikoto.aozora.controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlInjectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.mikoto.aozora.model.Artwork;
import net.mikoto.aozora.model.ArtworkIndex;
import net.mikoto.aozora.service.ArtworkIndexService;
import net.mikoto.aozora.service.ArtworkService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author mikoto
 * &#064;date 2024/1/19
 * Create for aozora
 */
@RestController
@RequestMapping("/api/index")
public class ArtworkIndexRestController {
    private final ArtworkIndexService artworkIndexService;
    private final ArtworkService artworkService;

    public ArtworkIndexRestController(ArtworkIndexService artworkIndexService, ArtworkService artworkService) {
        this.artworkIndexService = artworkIndexService;
        this.artworkService = artworkService;
    }

    @RequestMapping("getIndices")
    public JSONObject getArtworkIndices(@RequestParam String key,
                                        @RequestParam int page,
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
        QueryWrapper<ArtworkIndex> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("distinct artwork_id");
        queryWrapper.eq("artwork_index_id", ArtworkIndex.getId(key));
        System.out.println(ArtworkIndex.getId(key));
        queryWrapper.le("grading", grading + 1);

        Page<ArtworkIndex> artworkIndexPage = new Page<>(page, 12);
        artworkIndexPage.addOrder(new OrderItem(column, "asc".equals(type)));

        Page<ArtworkIndex> resultArtworkIndexPage = artworkIndexService.page(artworkIndexPage, queryWrapper);

        if (resultArtworkIndexPage.getSize() == 0) {
            result.put("success", false);
            result.put("msg", "Page is out of index or unaviliable grading or column");
        } else {
            result.put("success", true);
            result.put("msg", "");
        }

        List<Artwork> artworks = new ArrayList<>();

        for (ArtworkIndex artworkIndex : resultArtworkIndexPage.getRecords()) {
            artworks.add(artworkService.getById(artworkIndex.getArtworkId()));
        }

        result.put("body", artworks);

        Date endDate = new Date();
        result.put("timeCost", ((double) (endDate.getTime() - startDate.getTime()) / 1000.00) + "s");
        return result;
    }
}
