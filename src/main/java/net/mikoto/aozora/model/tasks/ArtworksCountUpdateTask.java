package net.mikoto.aozora.model.tasks;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
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

    @Override
    public void run() {
        cache = (int) artworkService.count();
        artworkService.setCachedArtworksCount(cache);
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
