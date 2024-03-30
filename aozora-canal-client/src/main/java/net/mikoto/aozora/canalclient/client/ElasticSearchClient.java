package net.mikoto.aozora.canalclient.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.Post;
import net.mikoto.aozora.canalclient.model.ArtworkIndex;
import org.springframework.stereotype.Component;

/**
 * @author mikoto
 * &#064;date 2024/3/24
 * Create for aozora
 */
@Component
@BaseRequest(
        baseURL = "http://192.168.123.90:9200/aozora"
)
public interface ElasticSearchClient {
    @Post(
            "/_doc"
    )
    String createIndex(@JSONBody ArtworkIndex artworkIndex);
}
