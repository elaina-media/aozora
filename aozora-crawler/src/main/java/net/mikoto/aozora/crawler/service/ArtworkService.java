package net.mikoto.aozora.crawler.service;

import com.mybatisflex.core.service.IService;
import net.mikoto.aozora.model.Artwork;

/**
 * @author mikoto
 * &#064;date 2023/11/16
 * Create for aozora
 */
public interface ArtworkService extends IService<Artwork> {
    Artwork getRemoteArtwork(int artworkId);
}
