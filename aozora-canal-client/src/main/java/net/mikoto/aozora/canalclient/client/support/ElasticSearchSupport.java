package net.mikoto.aozora.canalclient.client.support;


import com.alibaba.fastjson2.JSONObject;
import lombok.extern.java.Log;
import net.mikoto.aozora.canalclient.client.ElasticSearchClient;
import net.mikoto.aozora.canalclient.model.ArtworkIndex;

/**
 * @author mikoto
 * &#064;date 2024/3/24
 * Create for aozora
 */
@Log
public class ElasticSearchSupport extends Support {
    private final ElasticSearchClient elasticSearchClient;

    public ElasticSearchSupport(Support next, ElasticSearchClient elasticSearchClient) {
        super(next);
        this.elasticSearchClient = elasticSearchClient;
    }

    @Override
    protected boolean resolve(JSONObject before, JSONObject after) {
        ArtworkIndex artworkIndex = new ArtworkIndex();

        artworkIndex.setTags(after.getString("tags"));
        artworkIndex.setArtworkTitle(after.getString("artwork_title"));
        artworkIndex.setAuthorName(after.getString("author_name"));
        artworkIndex.setAuthorId(Long.parseLong(after.getString("author_id")));
        artworkIndex.setSeriesId(Long.parseLong(after.getString("series_id")));
        artworkIndex.setGrading(Integer.parseInt(after.getString("grading")));
        artworkIndex.setBookmarkCount(Integer.parseInt(after.getString("bookmark_count")));
        artworkIndex.setLikeCount(Integer.parseInt(after.getString("like_count")));
        artworkIndex.setViewCount(Integer.parseInt(after.getString("view_count")));
        artworkIndex.setAi("1".equals(after.getString("is_ai")));
        artworkIndex.setManga("1".equals(after.getString("is_manga")));

        log.info(elasticSearchClient.createIndex(artworkIndex));
        return true;
    }
}
