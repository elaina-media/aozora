package net.mikoto.aozora.service;

import com.mybatisflex.core.service.IService;
import net.mikoto.aozora.model.Artwork;
import net.mikoto.aozora.model.ExtensionTag;

/**
 * @author mikoto
 * &#064;date 2023/11/16
 * Create for aozora
 */
public interface ArtworkService extends IService<Artwork> {
    Artwork getRemoteArtwork(int artworkId);
    ExtensionTag[] getExtensionTags(String tag, String lang);
    byte[] getImage(String url);
    int getCachedArtworksCount();
    void setCachedArtworksCount(int artworksCount);

    enum tagLang {
        zh
    }
}
