package net.mikoto.aozora.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import net.mikoto.aozora.model.ArtworkIndex;

/**
 * @author mikoto
 * &#064;date 2024/1/4
 * Create for aozora
 */
public interface ArtworkIndexService extends IService<ArtworkIndex> {
    Page<Integer> getArtworksPaginate(int size, int page, String orderingColumn, String orderingType, QueryWrapper queryWrapper);
}
