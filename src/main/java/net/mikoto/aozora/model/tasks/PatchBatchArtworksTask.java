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
            if (artwork != null) {
                cache.add(artwork);
            }
            if (cache.size() >= 100) {
                artworkService.saveOrUpdateBatch(cache);
                cache.clear();
            }
            log.info("Finish work: " + artworkId);
            if (artwork == null) {
                log.info("With null data");
            }
            doneWorkCount++;
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
