package net.mikoto.aozora.model.tasks;

import com.dtflys.forest.exceptions.ForestRuntimeException;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.mikoto.aozora.model.Artwork;
import net.mikoto.aozora.service.ArtworkService;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author mikoto
 * &#064;date 2023/11/17
 * Create for aozora
 */
@Data
@Log
public class PatchBatchArtworksTask implements Runnable {
    private final Integer startId;
    private final Integer endId;
    private ArtworkService artworkService;
    private int doneWorkCount;
    private int notNullWorkCount;
    private int nullWorkCount;
    private int totalNotNullWorkCount;
    private int totalNullWorkCount;
    private final Set<Artwork> cache = new HashSet<>();

    public PatchBatchArtworksTask(Integer startId, Integer endId) {
        this.startId = startId;
        this.endId = endId;
    }

    @SneakyThrows
    @Override
    public void run() {
        while (startId + doneWorkCount <= endId) {
            int artworkId = startId + doneWorkCount;
            Artwork artwork = doGet(artworkId);
            if (cache.isEmpty() && nullWorkCount == 0) {
                log.info("缓存计数器位于: " + artworkId + " -> " + (artworkId + 100));
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
            if (cache.size() >= 100) {
                artworkService.saveOrUpdateBatch(cache);
                cache.clear();
                log.info("缓存更新于: " + artworkId + " -> " + (artworkId + 100));
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
        }
        log.info("Finish task: " + startId + " -> " + endId);
    }

    private Artwork doGet(int artworkId) throws InterruptedException {
        Artwork artwork = null;
        try {
            artwork = artworkService.getRemoteArtwork(artworkId);
        } catch (ForestRuntimeException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                log.info("Time out at task: " + startId + " -> " + endId);
                Thread.sleep(10000);
                log.info("Restart patch at: " + artworkId);
                doGet(artworkId);
            }
        }
        return artwork;
    }
}
