package net.mikoto.aozora.canalclient.client;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.Message;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.mikoto.aozora.canalclient.mapper.ArtworkIndexMapper;
import net.mikoto.aozora.canalclient.mapper.ArtworkMapper;
import net.mikoto.aozora.canalclient.service.ArtworkIndexService;
import net.mikoto.aozora.model.Artwork;
import net.mikoto.aozora.model.ArtworkIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    private final ArtworkIndexMapper artworkIndexMapper;
    private final ArtworkMapper artworkMapper;
    private static final int BATCH_SIZE = 10;

    @Autowired
    public CanalClient(CanalConnector canalConnector, ArtworkIndexService artworkIndexService, ArtworkIndexMapper artworkIndexMapper, ArtworkMapper artworkMapper) {
        this.connector = canalConnector;
        this.artworkIndexService = artworkIndexService;
        this.artworkIndexMapper = artworkIndexMapper;
        this.artworkMapper = artworkMapper;
    }

    @Override
    @SneakyThrows
    @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
    public void afterPropertiesSet() {
//        //打开连接
//        connector.connect();
//        //订阅数据库表,全部表
//        connector.subscribe("aozora\\..*");
//        //回滚到未进行ack的地方，下次fetch的时候，可以从最后一个没有ack的地方开始拿
//        connector.rollback();
//
//        while (true) {
//            // 获取指定数量的数据
//            Message message = connector.getWithoutAck(BATCH_SIZE);
//            //获取批量ID
//            long batchId = message.getId();
//            //获取批量的数量
//            int size = message.getEntries().size();
//
//            if (batchId == -1 || size == 0) {
//                Thread.sleep(2000);
//            } else {
//                scanMessages(message.getEntries());
//            }
//
//            //进行 batch id 的确认。确认之后，小于等于此 batchId 的 Message 都会被确认。
//            connector.ack(batchId);
//
//
//        }

//        QueryWrapper<Artwork> artworkWrapper = new QueryWrapper<>();
//        List<Artwork> artworks = artworkMapper.selectList(artworkWrapper);
//        for (Artwork artwork : artworks) {
//            ArtworkIndex artworkIndex = new ArtworkIndex();
//            for (String tag : artwork.getTags().split(";")) {
//
//                artworkIndex.setKey(tag);
//                artworkIndex.setGrading(artwork.getGrading());
//                artworkIndex.setArtworkId(artwork.getArtworkId());
//                artworkIndex.setBookmarkCount(artwork.getBookmarkCount());
//                artworkIndex.setLikeCount(artwork.getLikeCount());
//                artworkIndex.setViewCount(artwork.getViewCount());
//
//            }
//            System.out.println(artwork.getArtworkId());
//            artworkIndexMapper.insert(artworkIndex);
//        }
        ArtworkIndex artworkIndex = new ArtworkIndex(5000L, 1, Artwork.Grading.General, 1, 1, 1);
        artworkIndexMapper.insert(artworkIndex);
    }

    @SneakyThrows
    private void scanMessages(@NotNull List<Entry> entries) {
        Set<ArtworkIndex> artworkIndices = new HashSet<>();

        for (Entry entry : entries) {
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND) {
                //开启/关闭事务的实体类型，跳过
                continue;
            }

            RowChange rowChange = RowChange.parseFrom(entry.getStoreValue());
            EventType eventType = rowChange.getEventType();

            for (RowData rowData : rowChange.getRowDatasList()) {
                if (eventType == EventType.INSERT) {
                    Set<ArtworkIndex> singleArtworkIndices = createIndex(rowData.getAfterColumnsList());

                    if (singleArtworkIndices != null) {
                        artworkIndices.addAll(singleArtworkIndices);
                    }
                }
            }
        }

        System.out.println("entities:" + entries.size());
        artworkIndexService.saveOrUpdateBatch(artworkIndices);
    }

    private @Nullable Set<ArtworkIndex> createIndex(@NotNull List<CanalEntry.Column> columns) {
        Set<ArtworkIndex> artworkIndices = new HashSet<>();
        int artworkId = -1, bookmarkCount = -1, likeCount = -1, viewCount = 0;
        Artwork.Grading grading = null;
        String[] tags = null;

        for (CanalEntry.Column column : columns) {
            switch (column.getName()) {
                case "tags" -> tags = column.getValue().split(";");
                case "artworkId" -> artworkId = Integer.parseInt(column.getValue());
                case "bookmarkCount" -> bookmarkCount = Integer.parseInt(column.getValue());
                case "likeCount" -> likeCount = Integer.parseInt(column.getValue());
                case "viewCount" -> viewCount = Integer.parseInt(column.getValue());
                case "grading" -> grading = Artwork.Grading.getGrading(Integer.parseInt(column.getValue()));
            }
        }

        System.out.println("columns:" + columns.size());

        if (tags == null || artworkId == -1) {
            return null;
        }

        for (String tag : tags) {
            ArtworkIndex artworkIndex = new ArtworkIndex();
            artworkIndex.setKey(tag);
            artworkIndex.setArtworkId(artworkId);
            artworkIndex.setGrading(grading);
            artworkIndex.setBookmarkCount(bookmarkCount);
            artworkIndex.setLikeCount(likeCount);
            artworkIndex.setViewCount(viewCount);

            artworkIndices.add(artworkIndex);

            System.out.println(tag + ": " + artworkIndex.getKey());
        }

        return artworkIndices;
    }
}
