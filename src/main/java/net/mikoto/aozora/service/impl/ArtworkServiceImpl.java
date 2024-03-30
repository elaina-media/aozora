package net.mikoto.aozora.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.dtflys.forest.exceptions.ForestNetworkException;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import net.mikoto.aozora.client.PixivClient;
import net.mikoto.aozora.mapper.ArtworkMapper;
import net.mikoto.aozora.model.Artwork;
import net.mikoto.aozora.service.ArtworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author mikoto
 * &#064;date 2024/3/31
 * Create for aozora
 */
@Service("ArtworkService")
public class ArtworkServiceImpl
        extends ServiceImpl<ArtworkMapper, Artwork>
        implements ArtworkService {
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
                String rawData = pixivClient.getArtwork(artworkId);
                JSONObject artworkRawData = JSON.parseObject(rawData);
                artwork = Artwork.parseFromRawJson(artworkRawData);
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
}
