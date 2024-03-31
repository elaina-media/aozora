package net.mikoto.aozora.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.SneakyThrows;
import net.mikoto.aozora.client.ElasticSearchClient;
import net.mikoto.aozora.service.SearchService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mikoto
 * &#064;date 2024/3/27
 * Create for aozora
 */
@Service
public class SearchServiceImpl implements SearchService {
    private final ElasticSearchClient elasticSearchClient;

    public SearchServiceImpl(ElasticSearchClient elasticSearchClient) {
        this.elasticSearchClient = elasticSearchClient;
    }

    private List<Integer> nullableSearch(
            int grading,
            boolean isAi,
            boolean isManga,
            String ordering,
            String orderingBy,
            int size,
            int page
    ) {
        JSONObject searchJson = new JSONObject();
        searchJson.fluentPut("from", size * (page - 1));
        searchJson.fluentPut("size", size);
        searchJson.fluentPut("_source", "artworkId");

        JSONObject queryJson = new JSONObject();
        searchJson.fluentPut("query", queryJson);

        JSONArray sortJson = new JSONArray();
        searchJson.fluentPut("sort", sortJson);

        JSONArray filterJson = new JSONArray();
        JSONObject boolJson = new JSONObject();

        queryJson.fluentPut("bool", boolJson);
        boolJson.fluentPut("filter", filterJson);

        filterJson.fluentAdd(
                new JSONObject()
                        .fluentPut("range", new JSONObject()
                                .fluentPut("grading", new JSONObject().fluentPut("lte", grading))
                        )
        );
        filterJson.fluentAdd(new JSONObject().fluentPut("match", new JSONObject().fluentPut("ai", isAi)));
        filterJson.fluentAdd(new JSONObject().fluentPut("match", new JSONObject().fluentPut("manga", isManga)));

        sortJson.fluentAdd(
                new JSONObject()
                        .fluentPut(
                                orderingBy, new JSONObject()
                                        .fluentPut("order", ordering)
                        )
        );

        List<Integer> artworkIds = new ArrayList<>();
        JSONObject searchResultJson = JSONObject.parseObject(elasticSearchClient.search(searchJson.toJSONString()));
        JSONArray hits = searchResultJson.getJSONObject("hits").getJSONArray("hits");
        for (int i = 0; i < hits.size(); i++) {
            JSONObject hit = hits.getJSONObject(i);
            artworkIds.add(hit.getJSONObject("_source").getIntValue("artworkId"));
        }
        return artworkIds;
    }

    @Override
    @SneakyThrows
    public List<Integer> search(
            String query,
            int grading,
            boolean isAi,
            boolean isManga,
            String ordering,
            String orderingBy,
            int size,
            int page
    ) {
        if ("$NULL".equals(query)) {
            return nullableSearch(grading, isAi, isManga, ordering, orderingBy, size, page);
        }
        JSONObject searchJson = new JSONObject();
        searchJson.fluentPut("from", size * (page - 1));
        searchJson.fluentPut("size", size);
        searchJson.fluentPut("_source", "artworkId");

        JSONObject queryJson = new JSONObject();
        searchJson.fluentPut("query", queryJson);

        JSONArray sortJson = new JSONArray();
        searchJson.fluentPut("sort", sortJson);

        JSONArray filterJson = new JSONArray();
        JSONObject boolJson = new JSONObject();
        JSONArray shouldJson = new JSONArray();

        queryJson.fluentPut("bool", boolJson);
        boolJson.fluentPut("should", shouldJson);
        boolJson.fluentPut("filter", filterJson);
        boolJson.fluentPut("minimum_should_match", 1);

        shouldJson.fluentAdd(new JSONObject().fluentPut("match", new JSONObject().fluentPut("tags", query)));
        shouldJson.fluentAdd(new JSONObject().fluentPut("match", new JSONObject().fluentPut("artworkTitle", query)));

        filterJson.fluentAdd(
                new JSONObject()
                        .fluentPut("range", new JSONObject()
                                .fluentPut("grading", new JSONObject().fluentPut("lte", grading))
                        )
        );
        filterJson.fluentAdd(new JSONObject().fluentPut("match", new JSONObject().fluentPut("ai", isAi)));
        filterJson.fluentAdd(new JSONObject().fluentPut("match", new JSONObject().fluentPut("manga", isManga)));

        sortJson.fluentAdd(
                new JSONObject()
                        .fluentPut(
                                orderingBy, new JSONObject()
                                        .fluentPut("order", ordering)
                        )
        );

        List<Integer> artworkIds = new ArrayList<>();
        JSONObject searchResultJson = JSONObject.parseObject(elasticSearchClient.search(searchJson.toJSONString()));
        JSONArray hits = searchResultJson.getJSONObject("hits").getJSONArray("hits");
        for (int i = 0; i < hits.size(); i++) {
            JSONObject hit = hits.getJSONObject(i);
            artworkIds.add(hit.getJSONObject("_source").getIntValue("artworkId"));
        }
        return artworkIds;
    }

    @Override
    public int artworkCount() {
        return Integer.parseInt(elasticSearchClient.getCount().split(" ")[2].replace("\n", ""));
    }
}
