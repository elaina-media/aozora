package net.mikoto.aozora.crawler.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.dtflys.forest.exceptions.ForestNetworkException;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.mikoto.aozora.crawler.client.PixivClient;
import net.mikoto.aozora.crawler.mapper.ArtworkMapper;
import net.mikoto.aozora.crawler.service.ArtworkService;
import net.mikoto.aozora.crawler.model.Artwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author mikoto
 * &#064;date 2023/11/16
 * Create for aozora
 */
@Log
@Service
public class ArtworkServiceImpl
        extends ServiceImpl<ArtworkMapper, Artwork>
        implements ArtworkService {
    // Beans
    private final PixivClient pixivClient;

    @Autowired
    public ArtworkServiceImpl(PixivClient pixivClient) {
        this.pixivClient = pixivClient;
    }

    @SneakyThrows
    @Override
    public Artwork getRemoteArtwork(int artworkId) {
        Artwork artwork;
        try {
            String rawData = pixivClient.getArtwork(artworkId);
            JSONObject artworkRawData = JSON.parseObject(rawData);
            artwork = Artwork.parseFromRawJson(artworkRawData);
        } catch (ForestNetworkException e) {
            if (e.getStatusCode() == 404) {
                return null;
            } else if (e.getStatusCode() == 429) {
                Thread.sleep(60000);
                artwork = getRemoteArtwork(artworkId);
            } else {
                throw e;
            }
        }

        return artwork;
    }
}
