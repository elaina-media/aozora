package net.mikoto.aozora.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtflys.forest.exceptions.ForestNetworkException;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.mikoto.aozora.client.PixivClient;
import net.mikoto.aozora.mapper.ArtworkMapper;
import net.mikoto.aozora.model.AozoraConfig;
import net.mikoto.aozora.model.Artwork;
import net.mikoto.aozora.service.ArtworkService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringJoiner;

/**
 * @author mikoto
 * &#064;date 2023/11/16
 * Create for aozora
 */
@Component
@Log
public class ArtworkServiceImpl extends ServiceImpl<ArtworkMapper, Artwork> implements ArtworkService {
    private final PixivClient pixivClient;
    private final AozoraConfig defaultConfig;
    /**
     * Pixiv usual date format.
     * <p>
     * e.g.
     * 2020-03-05T01:23:36+00:00
     * 2021-07-15T15:48:17+00:00
     */
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    private static final String PIXIV_IMAGE_URL = "https://i.pximg.net";
    private static int sessionIdCount = 0;
    private static int getCount = 0;
    @Setter
    @Getter
    private int cachedArtworksCount;
    @Autowired
    public ArtworkServiceImpl(PixivClient pixivClient, AozoraConfig defaultConfig) {
        this.pixivClient = pixivClient;
        this.defaultConfig = defaultConfig;
    }

    @SneakyThrows
    @Override
    public Artwork getRemoteArtwork(int artworkId) {
        // 计数器刷新&归零&切换SessionId
        getCount++;
        if (getCount >= 5) {
            getCount = 0;
            sessionIdCount++;
            if (sessionIdCount >= defaultConfig.getPhpSessionId().length) {
                sessionIdCount = 0;
            }
        }

        Artwork artwork;
        try {
            artwork = getRemoteArtwork(artworkId, defaultConfig.getPhpSessionId()[sessionIdCount]);
        } catch (ForestNetworkException e) {
            if (e.getStatusCode() == 404) {
                return null;
            } else if (e.getStatusCode() == 429) {
                log.info(
                        "The session id temporary unavailable: " +
                                defaultConfig.getPhpSessionId()[sessionIdCount] +
                                " at artwork id :" +
                                artworkId
                );
                sessionIdCount++;
                if (sessionIdCount >= defaultConfig.getPhpSessionId().length) {
                    sessionIdCount = 0;
                }
                Thread.sleep(60000);
                log.info("Restart patch with: " + defaultConfig.getPhpSessionId()[sessionIdCount]);
                artwork = getRemoteArtwork(artworkId, defaultConfig.getPhpSessionId()[sessionIdCount]);
            } else {
                throw e;
            }
        }

        return artwork;
    }

    @Override
    public byte[] getImage(String url) {
        return pixivClient.getImage(url);
    }

    @SneakyThrows
    @Override
    public Artwork getRemoteArtwork(int artworkId, String sessionId) {
        String rawData = pixivClient.getArtwork(artworkId, "PHPSESSID=" + sessionId);
        JSONObject artworkRawData = JSON.parseObject(rawData);


        return Artwork.parseFromRawJson(artworkRawData);
    }

    private void nextSessionId(@NotNull AozoraConfig config) {
        sessionIdCount++;
        if (sessionIdCount >= config.getPhpSessionId().length) {
            sessionIdCount = 0;
        }
    }
}
