package net.mikoto.aozora.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.mikoto.aozora.model.Artwork;

/**
 * @author mikoto
 * &#064;date 2023/11/16
 * Create for aozora
 */
public interface ArtworkService extends IService<Artwork> {
    Artwork getRemoteArtwork(int artworkId);
}
