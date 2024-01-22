package net.mikoto.aozora.canalclient.client;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.alibaba.otter.canal.client.CanalConnector;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.mikoto.aozora.canalclient.mapper.ArtworkIndexMapper;
import net.mikoto.aozora.canalclient.mapper.ArtworkMapper;
import net.mikoto.aozora.canalclient.service.ArtworkIndexService;
import net.mikoto.aozora.canalclient.service.ArtworkService;
import net.mikoto.aozora.model.Artwork;
import net.mikoto.aozora.model.ArtworkIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author mikoto
 * &#064;date 2024/1/14
 * Create for aozora
 */
@Component
@Log
public class CanalClient implements InitializingBean {
    private final CanalConnector connector;
    private final ArtworkIndexService artworkIndexService;
    private final ArtworkService artworkService;
    private final ArtworkMapper artworkMapper;
    private final ArtworkIndexMapper artworkIndexMapper;
    private static final int BATCH_SIZE = 10;

    @Autowired
    public CanalClient(CanalConnector canalConnector, ArtworkIndexService artworkIndexService, ArtworkService artworkService, ArtworkMapper artworkMapper, ArtworkIndexMapper artworkIndexMapper) {
        this.connector = canalConnector;
        this.artworkIndexService = artworkIndexService;
        this.artworkService = artworkService;
        this.artworkMapper = artworkMapper;
        this.artworkIndexMapper = artworkIndexMapper;
    }

//    @SneakyThrows
//    private void scanMessages(@NotNull List<Entry> entries) {
//        Set<ArtworkIndex> artworkIndices = new HashSet<>();
//
//        for (Entry entry : entries) {
//            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND) {
//                //开启/关闭事务的实体类型，跳过
//                continue;
//            }
//
//            RowChange rowChange = RowChange.parseFrom(entry.getStoreValue());
//            EventType eventType = rowChange.getEventType();
//
//            for (RowData rowData : rowChange.getRowDatasList()) {
//                if (eventType == EventType.INSERT) {
//                    Set<ArtworkIndex> singleArtworkIndices = createIndex(rowData.getAfterColumnsList());
//
//                    if (singleArtworkIndices != null) {
//                        artworkIndices.addAll(singleArtworkIndices);
//                    }
//                }
//            }
//        }
//
//        System.out.println("entities:" + entries.size());
//        artworkIndexService.saveOrUpdateBatch(artworkIndices);
//    }
//
//    private @Nullable Set<ArtworkIndex> createIndex(@NotNull List<CanalEntry.Column> columns) {
//        Set<ArtworkIndex> artworkIndices = new HashSet<>();
//        int artworkId = -1, bookmarkCount = -1, likeCount = -1, viewCount = 0;
//        Artwork.Grading grading = null;
//        String[] tags = null;
//
//        for (CanalEntry.Column column : columns) {
//            switch (column.getName()) {
//                case "tags" -> tags = column.getValue().split(";");
//                case "artworkId" -> artworkId = Integer.parseInt(column.getValue());
//                case "bookmarkCount" -> bookmarkCount = Integer.parseInt(column.getValue());
//                case "likeCount" -> likeCount = Integer.parseInt(column.getValue());
//                case "viewCount" -> viewCount = Integer.parseInt(column.getValue());
//                case "grading" -> grading = Artwork.Grading.getGrading(Integer.parseInt(column.getValue()));
//            }
//        }
//
//        System.out.println("columns:" + columns.size());
//
//        if (tags == null || artworkId == -1) {
//            return null;
//        }
//
//        for (String tag : tags) {
//            ArtworkIndex artworkIndex = new ArtworkIndex();
//            artworkIndex.setKey(tag);
//            artworkIndex.setArtworkId(artworkId);
//            artworkIndex.setGrading(grading);
//            artworkIndex.setBookmarkCount(bookmarkCount);
//            artworkIndex.setLikeCount(likeCount);
//            artworkIndex.setViewCount(viewCount);
//
//            artworkIndices.add(artworkIndex);
//
//            System.out.println(tag + ": " + artworkIndex.getArtworkIndexId());
//        }
//
//        return artworkIndices;
//    }

    @Override
    public void afterPropertiesSet() {
        QueryWrapper<Artwork> artworkWrapper = new QueryWrapper<>();
        artworkWrapper.select("artwork_id");
        List<Object> artworks = artworkMapper.selectObjs(artworkWrapper);
        for (Object artworkId : artworks) {
            Artwork artwork = artworkMapper.selectById((Serializable) artworkId);
            for (String tag : artwork.getTags().split(";")) {
                ArtworkIndex artworkIndex = new ArtworkIndex();

                artworkIndex.setKey(tag);
                artworkIndex.setGrading(artwork.getGrading());
                artworkIndex.setArtworkId(artwork.getArtworkId());
                artworkIndex.setBookmarkCount(artwork.getBookmarkCount());
                artworkIndex.setLikeCount(artwork.getLikeCount());
                artworkIndex.setViewCount(artwork.getViewCount());

                artworkIndexMapper.insert(artworkIndex);

                String result = artwork.getArtworkId() + ":" + tag + ":" + artworkIndex.getArtworkIndexId();
                System.out.println(result);
            }
        }
    }
}
