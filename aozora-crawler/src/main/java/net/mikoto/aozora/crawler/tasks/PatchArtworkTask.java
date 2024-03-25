package net.mikoto.aozora.crawler.tasks;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.mikoto.aozora.crawler.model.Observer;
import net.mikoto.aozora.crawler.service.ArtworkService;
import net.mikoto.aozora.model.Artwork;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author mikoto
 * &#064;date 2024/1/22
 * Create for aozora
 *
 *
 */
@Log
public class PatchArtworkTask implements Runnable, Observer {
    @Setter
    private ArtworkService artworkService;

    private final int beginningArtwork;
    private final int endingArtwork;
    private int doneWorkCount;
    private int totalNotNullWorkCount;
    private int totalNullWorkCount;
    private int notNullWorkCount;
    private int nullWorkCount;
    private final Set<Artwork> cache = new HashSet<>();
    private final int cacheSize;
    private final int delay;

    private long startTime;
    private int totalQps;

    private int currentQps;
    private int currentGetCount;
    private long currentStartTime;
    private long currentLastGetTime;

    public PatchArtworkTask(String beginningArtwork, String endingArtwork, String cacheSize, String delay) {
        this.beginningArtwork = Integer.parseInt(beginningArtwork);
        this.endingArtwork = Integer.parseInt(endingArtwork);
        this.cacheSize = Integer.parseInt(cacheSize);
        this.delay = Integer.parseInt(delay);
    }

    @SneakyThrows
    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        currentStartTime = startTime;

        while (beginningArtwork + doneWorkCount <= endingArtwork) {
            int artworkId = beginningArtwork + doneWorkCount;
            Artwork artwork = doGetArtwork(artworkId);
            if (cache.isEmpty() && nullWorkCount == 0) {
                log.info("缓存计数器位于: " + artworkId + " -> " + (artworkId + cacheSize));
            }

            if (artwork != null) {
                // 缓存更新&计数器更新：有效作业
                cache.add(artwork);
                totalNotNullWorkCount++;
                notNullWorkCount++;
            } else {
                // 计数器更新：无效作业
                totalNullWorkCount++;
                nullWorkCount++;
            }
            doneWorkCount++;
            if (cache.size() >= cacheSize) {
                for (Artwork cachedArtwork : cache) {
                    Artwork storagedArtwork = artworkService.getById(cachedArtwork.getArtworkId());
                    if (storagedArtwork == null) {
                        artworkService.save(cachedArtwork);
                    } else {
                        artworkService.updateById(cachedArtwork);
                    }
                }
                cache.clear();
                log.info("缓存更新于: " + artworkId);
                System.out.println("    总作业范围：" + beginningArtwork + " -> " + endingArtwork);
                System.out.println("    作业进度：" + (((double) doneWorkCount / (double) (endingArtwork - beginningArtwork)) * 100) + "%");
                System.out.println("    总作业数：" + doneWorkCount);
                System.out.println("    总有效作业数：" + totalNotNullWorkCount);
                System.out.println("        占比：" + (((double) totalNotNullWorkCount / (double) doneWorkCount) * 100) + "%");
                System.out.println("    总无效作业数：" + totalNullWorkCount);
                System.out.println("        占比：" + (((double) totalNullWorkCount / (double) doneWorkCount) * 100) + "%");
                System.out.println("    缓内作业数：" + (notNullWorkCount + nullWorkCount));
                System.out.println("    缓内有效作业数：" + notNullWorkCount);
                System.out.println("        占比：" + (((double) notNullWorkCount / (double) (notNullWorkCount + nullWorkCount)) * 100) + "%");
                System.out.println("    缓内无效作业数：" + nullWorkCount);
                System.out.println("        占比：" + (((double) nullWorkCount / (double) (notNullWorkCount + nullWorkCount)) * 100) + "%");
                System.out.println();

                // 计数器刷新：有效/无效作业
                notNullWorkCount = 0;
                nullWorkCount = 0;
            }

            currentLastGetTime = System.currentTimeMillis();
            currentGetCount++;
            if (currentLastGetTime - startTime > 1000) {
                currentQps = currentGetCount + 1;
                currentStartTime = currentLastGetTime;
                currentGetCount = 0;
            }

            totalQps = doneWorkCount;

            Thread.sleep(delay);
        }
    }

    @SneakyThrows
    private Artwork doGetArtwork(int artworkId) {
        Artwork artwork = null;
        try {
            artwork = artworkService.getRemoteArtwork(artworkId);
        } catch (ForestRuntimeException e) {
            if (e.getCause() instanceof IOException) {
                log.info("任务中断于：" + artworkId);
                Thread.sleep(10000);
                log.info("任务重启于: " + artworkId);
                doGetArtwork(artworkId);
            }
        }
        return artwork;
    }
}
