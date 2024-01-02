package net.mikoto.aozora.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.HTTPProxy;
import com.dtflys.forest.annotation.Var;
import net.mikoto.aozora.client.interceptor.CookieInterceptor;

/**
 * @author mikoto
 * &#064;date 2023/11/16
 * Create for aozora
 */
@BaseRequest(baseURL = "https://www.pixiv.net", interceptor = CookieInterceptor.class)
public interface PixivClient {
    @Get(
            url = "/ajax/illust/{artworkId}",
            headers = {
                    "Host: www.pixiv.net",
            },
            userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/119.0"
    )
    String getArtwork(@Var("artworkId") int artworkId);

    /**
     * Get the image from pixiv.
     *
     * @param url The image url.
     * @return Result.
     */
    @Get(
            url = "https://i.pximg.net{url}",
            headers = {
                    "Referer: https://www.pixiv.net"
            }
    )
    byte[] getImage(@Var("url") String url);

    @Get(
            url = "/rpc/cps.php?keyword={keyword}&lang={lang}&version={version}",
            headers = {
            "Host: www.pixiv.net"
            }
    )
    String getCps(@Var("keyword") String keyword, @Var("lang") String lang, @Var("version") String cpsVersion);
}
