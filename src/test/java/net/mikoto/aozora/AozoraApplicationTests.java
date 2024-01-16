package net.mikoto.aozora;

import lombok.extern.log4j.Log4j2;
import net.mikoto.aozora.mapper.ArtworkMapper;
import net.mikoto.aozora.model.ArtworkIndex;
import net.mikoto.aozora.service.ArtworkService;
import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
class AozoraApplicationTests {
	private final ArtworkService artworkService;
	private final ArtworkMapper artworkMapper;

	@Autowired
	AozoraApplicationTests(ArtworkService artworkService, ArtworkMapper artworkMapper) {
		this.artworkService = artworkService;
		this.artworkMapper = artworkMapper;
	}

	@Test
	void contextLoads() {
		log.log(Level.INFO, artworkService.getRemoteArtwork(20));
	}


	@Test
	void indexTest() {
		ArtworkIndex artworkIndex = new ArtworkIndex();
		artworkIndex.setKey("测试");
		System.out.println(artworkIndex.getKey());
	}

}
