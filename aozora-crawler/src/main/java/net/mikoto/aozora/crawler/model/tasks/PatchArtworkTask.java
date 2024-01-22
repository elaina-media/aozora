package net.mikoto.aozora.crawler.model.tasks;

import cn.hutool.cron.task.Task;
import net.mikoto.aozora.crawler.service.ArtworkService;

/**
 * @author mikoto
 * &#064;date 2024/1/22
 * Create for aozora
 */
public class PatchArtworkTask implements Task {
    private ArtworkService artworkService;
    private int beginningArtwork;
    private int endingArtwork;

    public PatchArtworkTask(String beginningArtwork, String endingArtwork) {
        this.beginningArtwork = Integer.parseInt(beginningArtwork);
        this.endingArtwork = Integer.parseInt(endingArtwork);
    }

    public void setArtworkService(ArtworkService artworkService) {
        this.artworkService = artworkService;
    }

    @Override
    public void execute() {

    }
}
