package net.mikoto.aozora.model.multipleTask;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import lombok.extern.java.Log;
import net.mikoto.aozora.client.PixivClient;
import net.mikoto.aozora.model.AozoraConfig;
import net.mikoto.aozora.model.Artwork;
import net.mikoto.aozora.service.ArtworkService;

import java.util.HashSet;
import java.util.Set;

/**
 * @author mikoto
 * &#064;date 2023/12/3
 * Create for aozora
 */
@Data
@Log
public class ArtworkMultipleTask implements MultipleTask {
    private ArtworkService artworkService;
    private PixivClient pixivClient;
    private AozoraConfig aozoraConfig;

    // Artwork prop
    private final Integer startId;
    private final Integer endId;
    private int doneWorkCount;
    private int totalNotNullWorkCount;
    private int totalNullWorkCount;

    private int notNullWorkCount;
    private int nullWorkCount;

    private int sessionIdCount;

    private final Set<Artwork> cache = new HashSet<>();
    private int hundredStepCounter;

    public ArtworkMultipleTask(Integer startId, Integer endId) {
        this.startId = startId;
        this.endId = endId;
    }

    @Override
    public Runnable popTask() {
        int artworkId = startId + doneWorkCount;
        String sessionId = aozoraConfig.getPhpSessionId()[sessionIdCount];

        doneWorkCount++;
        sessionIdCount++;

        // 退出loop
        if (artworkId > endId) {
            return null;
        }
        // SessionId计数器清零
        if (sessionIdCount > aozoraConfig.getPhpSessionId().length) {
            sessionIdCount = 0;
        }
        if (hundredStepCounter > 100) {
            artworkService.saveOrUpdateBatch(cache);
            cache.clear();
            log.info("百步缓存更新于: " + artworkId + " -> " + (artworkId + 100));
            System.out.println("    总作业数：" + doneWorkCount);
            System.out.println("        总有效作业数：" + totalNotNullWorkCount);
            System.out.println("        总无效作业数：" + totalNullWorkCount);
            System.out.println("    缓内作业数：" + hundredStepCounter);
            System.out.println("        缓内有效作业数：" + notNullWorkCount);
            System.out.println("        缓内无效作业数：" + nullWorkCount);
            System.out.println();

            // 计数器刷新：有效/无效作业
            notNullWorkCount = 0;
            nullWorkCount = 0;
            hundredStepCounter = 0;
        }

        return () -> {
            hundredStepCounter++;
            String rawData = pixivClient.getArtwork(artworkId, "PHPSESSID=" + sessionId);
            cache.add(Artwork.parseFromRawJson(JSON.parseObject(rawData)));
        };
    }
}
