package net.mikoto.aozora.controller;

import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import net.mikoto.aozora.model.Artwork;
import net.mikoto.aozora.model.ArtworkIndex;
import net.mikoto.aozora.model.DynamicConfig;
import net.mikoto.aozora.service.ArtworkIndexService;
import net.mikoto.aozora.service.ArtworkService;
import net.mikoto.aozora.utils.TimeCost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import static net.mikoto.aozora.model.table.Tables.ARTWORK;
import static net.mikoto.aozora.model.table.Tables.ARTWORK_INDEX;

/**
 * @author mikoto
 * &#064;date 2023/11/17
 * Create for aozora
 */
@RestController
@RequestMapping("/api/artwork")
public class ArtworkRestController {
    private final ArtworkService artworkService;
    private final DynamicConfig dynamicConfig;
    private final ArtworkIndexService artworkIndexService;

    @Autowired
    public ArtworkRestController(ArtworkService artworkService, DynamicConfig dynamicConfig, ArtworkIndexService artworkIndexService) {
        this.artworkService = artworkService;
        this.dynamicConfig = dynamicConfig;
        this.artworkIndexService = artworkIndexService;
    }

    @RequestMapping("/getRemoteArtwork")
    public JSONObject getRemoteArtwork(@RequestParam final int artworkId,
                                       @RequestParam final boolean isSave) {
        JSONObject result = new JSONObject();
        result.put(
                "timeCost",
                ((TimeCost) () -> {

                    Artwork artwork = artworkService.getRemoteArtwork(artworkId);
                    result.put("body", JSONObject.from(artwork));

                    if (isSave && artwork != null) {
                        artworkService.saveOrUpdate(artwork);
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
                ((TimeCost) () -> result.put("body", artworkService.count())).getTimeCost()
        );
        return result;
    }

    @RequestMapping("/getArtworks/{key}/{page}")
    public JSONObject getArtworks(@PathVariable final String key,
                                  @PathVariable final int page,
                                  @RequestParam final String orderingColumn,
                                  @RequestParam final String orderingType,
                                  @RequestParam final int grading) {
        JSONObject result = new JSONObject();


        result.put(
                "timeCost",
                ((TimeCost) () -> {

                    // 空Key查询
                    if ("$NULL".equals(key)) {
                        QueryChain<Artwork> artworkMapperQueryChain =
                                QueryChain.of(Artwork.class)
                                        .select(ARTWORK.ALL_COLUMNS)
                                        .le(Artwork::getGrading, grading + 1)
                                        .orderBy(orderingColumn, "asc".equals(orderingType));

                        Page<Artwork> artworkPage = new Page<>(page, 12);
                        artworkPage = artworkService.page(artworkPage, artworkMapperQueryChain.toQueryWrapper());

                        if (artworkPage != null) {
                            JSONObject body = new JSONObject();

                            body.put("artworks", artworkPage.getRecords());
                            body.put("pageCount", artworkPage.getPageNumber());
                            body.put("pageSize", artworkPage.getPageSize());
                            body.put("totalPage", artworkPage.getTotalPage());

                            result.put("body", body);
                        }
                    }

                    // 普通Key查询
                    else {

                        QueryChain<ArtworkIndex> artworkIndexMapperQueryChain =
                                QueryChain.of(ArtworkIndex.class)
                                        .select(ARTWORK_INDEX.ARTWORK_ID)
                                        .le(ArtworkIndex::getGrading, grading + 1)
                                        .orderBy(orderingColumn, "asc".equals(orderingType))
                                        .eq(ArtworkIndex::getArtworkIndexId, ArtworkIndex.getId(key));

                        Page<Integer> artworkPage = new Page<>(page, 12);

                        artworkPage = artworkIndexService.pageAs(
                                artworkPage,
                                artworkIndexMapperQueryChain.toQueryWrapper(),
                                Integer.class);

                        if (artworkPage != null) {
                            JSONObject body = new JSONObject();

                            body.put("artworks", artworkService.listByIds(artworkPage.getRecords()));
                            body.put("pageCount", artworkPage.getPageNumber());
                            body.put("pageSize", artworkPage.getPageSize());
                            body.put("totalPage", artworkPage.getTotalPage());

                            result.put("body", body);
                        }
                    }



                }).getTimeCost()
        );
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

    @RequestMapping("/getCps")
    public JSONObject getCps(@RequestParam String key, @RequestParam String lang) {
        JSONObject result = new JSONObject();
        result.put("body", artworkService.getExtensionTags(key, lang));
        return result;
    }

}
