package net.mikoto.aozora;

import lombok.extern.log4j.Log4j2;
import net.mikoto.aozora.service.ArtworkService;
import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
class AozoraApplicationTests {
	private final ArtworkService artworkService;

	@Autowired
	AozoraApplicationTests(ArtworkService artworkService) {
		this.artworkService = artworkService;
	}

	@Test
	void contextLoads() {
		log.log(Level.INFO, artworkService.getRemoteArtwork(20));
	}

}
