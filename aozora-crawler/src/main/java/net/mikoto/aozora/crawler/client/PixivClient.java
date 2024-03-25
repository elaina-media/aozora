package net.mikoto.aozora.crawler.client;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Var;
import net.mikoto.aozora.crawler.client.interceptor.CookieInterceptor;
import net.mikoto.aozora.crawler.client.interceptor.CpsVersionInterceptor;
import org.springframework.stereotype.Component;

/**
 * @author mikoto
 * &#064;date 2023/11/16
 * Create for aozora
 */
@Component
@BaseRequest(baseURL = "https://www.pixiv.net")
public interface PixivClient {
    @Get(
            url = "/ajax/illust/{artworkId}",
            headers = {
                    "Host: www.pixiv.net",
                    "Accept: application/json",
                    "Referer: https://www.pixiv.net/artworks/{artworkId}"
            },
            userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/119.0",
            interceptor = CookieInterceptor.class
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
                    "Host: www.pixiv.net",
                    "Referer: https://www.pixiv.net"
            }
    )
    byte[] getImage(@Var("url") String url);

    @Get(
            url = "/rpc/cps.php?keyword={keyword}&lang={lang}&version={version}",
            headers = {
                    "Host: www.pixiv.net"
            },
            userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/119.0",
            interceptor = {CookieInterceptor.class, CpsVersionInterceptor.class}
    )
    String getCps(@Var("keyword") String keyword, @Var("lang") String lang, @Var("version") String cpsVersion);
}
