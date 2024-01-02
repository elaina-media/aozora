package net.mikoto.aozora.model.multipleTask;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.mikoto.aozora.client.PixivClient;
import net.mikoto.aozora.model.AozoraConfig;
import net.mikoto.aozora.model.Artwork;
import net.mikoto.aozora.service.ArtworkService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.SocketTimeoutException;
import java.util.Date;
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

    private final long taskId;
    private final Date startDate;

    // Artwork prop
    private final Integer startId;
    private final Integer endId;
    private final Integer lookCount;
    private int doneWorkCount;
    private int totalNotNullWorkCount;
    private int totalNullWorkCount;

    private int cachedWorkCount;
    private int notNullWorkCount;
    private int nullWorkCount;

    private int sessionIdCount;

    private final Set<Artwork> cache = new HashSet<>();

    public ArtworkMultipleTask(Integer startId, Integer endId, Integer lookCount) {
        this.startId = startId;
        this.endId = endId;
        this.lookCount = lookCount;
        this.taskId = IdUtil.getSnowflakeNextId();
        this.startDate = new Date();
        log.info("ArtworkMultipleTask成功装载于: " + taskId);
    }

    @Override
    public SingleTask popTask() {
        int artworkId = startId + doneWorkCount;
        String sessionId = aozoraConfig.getPhpSessionId()[sessionIdCount];

        // 任务完成
        if (artworkId > endId) {
            // 日志
            log.info("任务完成于: " + startId + " -> " + endId);
            System.out.println("    任务ID：" + taskId);
            printBaseTaskInfo();
            System.out.println();

            return null;
        }

        doneWorkCount++;
        sessionIdCount++;

        return new SingleTask() {
            boolean flag = false;

            @Override
            public boolean isFinished() {
                return flag;
            }

            @Override
            public void run() {
                cachedWorkCount++;

                // SessionId计数器清零
                if (sessionIdCount > aozoraConfig.getPhpSessionId().length) {
                    sessionIdCount = 0;
                }

                // 缓存更新
                if (cachedWorkCount > 100) {
                    artworkService.saveOrUpdateBatch(cache);
                    cache.clear();
                    Date cacheEndDate = new Date();

                    // 日志
                    log.info("百步缓存更新于: " + (artworkId - 100) + " -> " + (artworkId - 1));
                    printBaseTaskInfo();
                    System.out.println("    缓内任务用时：" + ((double) (cacheEndDate.getTime() - startDate.getTime()) / 1000.00) + "s" + "s");
                    System.out.println("    缓内作业数：" + cachedWorkCount);
                    System.out.println("        缓内有效作业数：" + notNullWorkCount);
                    System.out.println("        缓内无效作业数：" + nullWorkCount);
                    System.out.println();

                    // 计数器刷新：有效/无效作业
                    notNullWorkCount = 0;
                    nullWorkCount = 0;
                    cachedWorkCount = 0;
                }

                cache.add(doPatchArtwork(artworkId, 0));
                flag = true;
            }
        };
    }

    @SneakyThrows
    private @Nullable Artwork doPatchArtwork(int artworkId, int loopCount) {
        Artwork artwork = null;
        try {
            String rawData = pixivClient.getArtwork(artworkId);
            artwork = Artwork.parseFromRawJson(JSON.parseObject(rawData));
        } catch (ForestRuntimeException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                if (loopCount > 5) {
                    return null;
                }

                log.info("任务超时于：" + artworkId);
                System.out.println("    任务ID：" + taskId);
                System.out.println("    任务起始作业：" + startId);
                System.out.println("    任务起始作业：" + endId);
                Thread.sleep(10000);
                log.info("任务重启于: " + artworkId);
                System.out.println("    任务ID：" + taskId);
                System.out.println("    任务起始作业：" + startId);
                System.out.println("    任务起始作业：" + endId);
                loopCount++;
                artwork = doPatchArtwork(artworkId, loopCount);
            }
        }
        return artwork;
    }

    private void printBaseTaskInfo() {
        printCurrentTime(new Date());
        System.out.println("    任务起始作业：" + startId);
        System.out.println("    任务起始作业：" + endId);
        System.out.println("    总作业数：" + doneWorkCount);
        System.out.println("        总有效作业数：" + totalNotNullWorkCount);
        System.out.println("        总无效作业数：" + totalNullWorkCount);
    }


    private void printCurrentTime(@NotNull Date endDate) {
        double timeCost = ((double) (endDate.getTime() - startDate.getTime()) / 1000.00);
        if (timeCost > 1000.00) {
            System.out.println("    任务用时：" + (timeCost / 60.00) + "min");
        } else {
            System.out.println("    任务用时：" + timeCost + "s");
        }
        System.out.println("    任务ID：" + taskId);
    }

    @Override
    public int getLoopCount() {
        return lookCount;
    }
}
