package net.mikoto.aozora.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.CacheableServiceImpl;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import net.mikoto.aozora.mapper.ArtworkIndexMapper;
import net.mikoto.aozora.model.Artwork;
import net.mikoto.aozora.model.ArtworkIndex;
import net.mikoto.aozora.service.ArtworkIndexService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import static net.mikoto.aozora.model.table.Tables.ARTWORK;
import static net.mikoto.aozora.model.table.Tables.ARTWORK_INDEX;

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
    @CacheEvict(allEntries = true)
    public boolean remove(QueryWrapper query) {
        return super.remove(query);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean removeByIds(Collection<? extends Serializable> ids) {
        return super.removeByIds(ids);
    }

    // 根据查询条件更新时，实体类主键可能为 null。
    @Override
    @CacheEvict(allEntries = true)
    public boolean update(ArtworkIndex entity, QueryWrapper query) {
        return super.update(entity, query);
    }

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(ArtworkIndex entity, boolean ignoreNulls) {
        return super.updateById(entity, ignoreNulls);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateBatch(Collection<ArtworkIndex> entities, int batchSize) {
        return super.updateBatch(entities, batchSize);
    }

    @Override
    @Cacheable(key = "#id")
    public ArtworkIndex getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public ArtworkIndex getOne(QueryWrapper query) {
        return super.getOne(query);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public <R> R getOneAs(QueryWrapper query, Class<R> asType) {
        return super.getOneAs(query, asType);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public List<ArtworkIndex> list(QueryWrapper query) {
        return super.list(query);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public <R> List<R> listAs(QueryWrapper query, Class<R> asType) {
        return super.listAs(query, asType);
    }

    // 无法通过注解进行缓存操作
    @Override
    @Deprecated
    public List<ArtworkIndex> listByIds(Collection<? extends Serializable> ids) {
        return super.listByIds(ids);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public long count(QueryWrapper query) {
        query.select(ARTWORK_INDEX.ARTWORK_INDEX_ID);
        return super.count(query);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #page.getPageSize() + ':' + #page.getPageNumber() + ':' + #query.toSQL()")
    public <R> Page<R> pageAs(Page<R> page, QueryWrapper query, Class<R> asType) {
        return super.pageAs(page, query, asType);
    }
}
