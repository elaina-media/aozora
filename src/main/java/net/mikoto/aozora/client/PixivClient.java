package net.mikoto.aozora.client;

import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.HTTPProxy;
import com.dtflys.forest.annotation.Var;

/**
 * @author mikoto
 * &#064;date 2023/11/16
 * Create for aozora
 */
@HTTPProxy(host = "127.0.0.1", port = "7891")
public interface PixivClient {
    @Get(
            url = "https://www.pixiv.net/ajax/illust/{artworkId}",
            headers = {
                    "Cookie: {cookie}",
                    "Host: www.pixiv.net",
            },
            userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/119.0"
    )
    String getArtwork(@Var("artworkId") int artworkId, @Var("cookie") String cookie);


}
