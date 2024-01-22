package net.mikoto.aozora.canalclient.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.java.Log;
import net.mikoto.aozora.canalclient.mapper.ArtworkMapper;
import net.mikoto.aozora.canalclient.service.ArtworkService;
import net.mikoto.aozora.model.Artwork;
import org.springframework.stereotype.Component;

/**
 * @author mikoto
 * &#064;date 2023/11/16
 * Create for aozora
 */
@Log
@Component
public class ArtworkServiceImpl
        extends ServiceImpl<ArtworkMapper, Artwork>
        implements ArtworkService {
}
