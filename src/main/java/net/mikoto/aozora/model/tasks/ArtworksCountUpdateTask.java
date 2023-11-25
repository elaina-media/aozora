package net.mikoto.aozora.model.tasks;

import lombok.Getter;
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
@Component
@Getter
public class ArtworksCountUpdateTask {
    private final ArtworkService artworkService;
    private int cache;

    public ArtworksCountUpdateTask(ArtworkService artworkService) {
        this.artworkService = artworkService;
        cache = (int) artworkService.count();
    }

    @Scheduled(cron = "* /20 * * * * ?")
    public void getArtworksCount() {
        cache = (int) artworkService.count();
    }
}
