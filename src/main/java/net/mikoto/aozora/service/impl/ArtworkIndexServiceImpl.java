package net.mikoto.aozora.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import net.mikoto.aozora.mapper.ArtworkIndexMapper;
import net.mikoto.aozora.model.ArtworkIndex;
import net.mikoto.aozora.service.ArtworkIndexService;
import org.springframework.stereotype.Service;

/**
 * @author mikoto
 * &#064;date 2024/1/4
 * Create for aozora
 */
@Service
public class ArtworkIndexServiceImpl
        extends ServiceImpl<ArtworkIndexMapper, ArtworkIndex>
        implements ArtworkIndexService {

    @Override
    public Page<Integer> getArtworksPaginate(int size, int pageCount, String orderingColumn, String orderingType, QueryWrapper queryWrapper) {
        return mapper.paginateAs(
                pageCount,
                size,
                queryWrapper,
                Integer.class
        );
    }
}
