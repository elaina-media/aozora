package net.mikoto.aozora.crawler.model.tasks;

import cn.hutool.cron.task.Task;
import com.alibaba.fastjson2.JSONObject;
import com.dtflys.forest.exceptions.ForestRuntimeException;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.mikoto.aozora.client.PixivClient;
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
public class PatchArtworkTask implements Task, Observer {
    @Setter
    private ArtworkService artworkService;
    @Setter
    private PixivClient pixivClient;


    private final int beginningArtwork;
    private final int endingArtwork;
    private int doneWorkCount;
    private int totalNotNullWorkCount;
    private int totalNullWorkCount;
    private int notNullWorkCount;
    private int nullWorkCount;
    private final Set<Artwork> cache = new HashSet<>();

    public PatchArtworkTask(String beginningArtwork, String endingArtwork) {
        this.beginningArtwork = Integer.parseInt(beginningArtwork);
        this.endingArtwork = Integer.parseInt(endingArtwork);
    }

    @Override
    public void execute() {
        while (beginningArtwork + doneWorkCount <= endingArtwork) {
            int artworkId = beginningArtwork + doneWorkCount;
            Artwork artwork = doGetArtwork(artworkId);
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
        }
    }

    @SneakyThrows
    private Artwork doGetArtwork(int artworkId) {
        Artwork artwork = null;
        try {
            JSONObject rawJsonArtworkData = JSONObject.parseObject(pixivClient.getArtwork(artworkId));
            artwork = Artwork.parseFromRawJson(rawJsonArtworkData);
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
