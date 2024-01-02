package net.mikoto.aozora.model.tasks;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import net.mikoto.aozora.client.PixivClient;
import net.mikoto.aozora.service.ArtworkService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author mikoto
 * &#064;date 2023/11/25
 * Create for aozora
 */
@Log
@Getter
public class ArtworksCountUpdateTask implements Runnable {
    @Setter
    private ArtworkService artworkService;
    private int cache;
    private final Long period;

    public ArtworksCountUpdateTask(Long period) {
        this.period = period;
    }

    @SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
    @Override
    public void run() {
        for (;;) {
            cache = (int) artworkService.count();
            artworkService.setCachedArtworksCount(cache);
            log.info("更新Artwork计数器缓存");
            try {
                Thread.sleep(period);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
