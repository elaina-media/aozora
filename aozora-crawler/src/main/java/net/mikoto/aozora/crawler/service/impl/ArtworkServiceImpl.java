package net.mikoto.aozora.crawler.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.mikoto.aozora.crawler.mapper.ArtworkMapper;
import net.mikoto.aozora.crawler.service.ArtworkService;
import net.mikoto.aozora.model.Artwork;
import org.springframework.stereotype.Service;

/**
 * @author mikoto
 * &#064;date 2024/1/7
 * Create for aozora
 */
@Service
public class ArtworkServiceImpl extends ServiceImpl<ArtworkMapper, Artwork> implements ArtworkService {
}
