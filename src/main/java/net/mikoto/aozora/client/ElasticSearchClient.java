package net.mikoto.aozora.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.JSONBody;
import com.dtflys.forest.annotation.Post;
import org.springframework.stereotype.Component;

/**
 * @author mikoto
 * &#064;date 2024/3/28
 * Create for aozora
 */
@Component
@BaseRequest(
        baseURL = "http://192.168.123.90:9200/aozora"
)
public interface ElasticSearchClient {
    @Post(
            "_search"
    )
    String search(@JSONBody String searchJson);

    @Get(
          "_cat/count/aozora"
    )
    String getCount();
}
