package net.mikoto.aozora.service;

import java.util.List;

/**
 * @author mikoto
 * &#064;date 2024/3/27
 * Create for aozora
 */
public interface SearchService {
    List<Integer> search(
            String query,
            int grading,
            boolean isAi,
            boolean isManga,
            String ordering,
            String orderingBy,
            int size,
            int page
    );

    int artworkCount();
}
