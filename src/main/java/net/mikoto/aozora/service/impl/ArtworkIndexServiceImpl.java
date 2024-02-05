package net.mikoto.aozora.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.CacheableServiceImpl;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import net.mikoto.aozora.mapper.ArtworkIndexMapper;
import net.mikoto.aozora.model.ArtworkIndex;
import net.mikoto.aozora.service.ArtworkIndexService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author mikoto
 * &#064;date 2024/1/4
 * Create for aozora
 */
@Service
@CacheConfig(cacheNames = "artworkIndex")
public class ArtworkIndexServiceImpl
        extends CacheableServiceImpl<ArtworkIndexMapper, ArtworkIndex>
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

    @Override
    @Cacheable(key = "#root.methodName + ':' + #page.getPageSize() + ':' + #page.getPageNumber() + ':' + #query.toSQL()")
    public <R> Page<R> pageAs(Page<R> page, QueryWrapper query, Class<R> asType) {
        return super.pageAs(page, query, asType);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public long count(QueryWrapper query) {
        return super.count(query);
    }
}
